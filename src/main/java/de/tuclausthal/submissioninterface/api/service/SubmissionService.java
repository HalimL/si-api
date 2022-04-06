package de.tuclausthal.submissioninterface.api.service;

import de.tuclausthal.submissioninterface.api.error.ErrorMessage;
import de.tuclausthal.submissioninterface.api.util.HibernateApiSessionManager;
import de.tuclausthal.submissioninterface.persistence.dao.DAOFactory;
import de.tuclausthal.submissioninterface.persistence.dao.ParticipationDAOIf;
import de.tuclausthal.submissioninterface.persistence.dao.SubmissionDAOIf;
import de.tuclausthal.submissioninterface.persistence.dao.TaskDAOIf;
import de.tuclausthal.submissioninterface.persistence.dao.UserDAOIf;
import de.tuclausthal.submissioninterface.persistence.dao.impl.LogDAO;
import de.tuclausthal.submissioninterface.persistence.datamodel.LogEntry;
import de.tuclausthal.submissioninterface.persistence.datamodel.Participation;
import de.tuclausthal.submissioninterface.persistence.datamodel.Submission;
import de.tuclausthal.submissioninterface.persistence.datamodel.Task;
import de.tuclausthal.submissioninterface.persistence.datamodel.Test;
import de.tuclausthal.submissioninterface.persistence.datamodel.UMLConstraintTest;
import de.tuclausthal.submissioninterface.persistence.datamodel.User;
import de.tuclausthal.submissioninterface.util.Configuration;
import de.tuclausthal.submissioninterface.util.Util;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.jvnet.hk2.annotations.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.json.Json;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.Part;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import java.io.File;
import java.io.IOException;
import java.lang.invoke.MethodHandles;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

@Service
public class SubmissionService {

    private Session session;
    private TaskDAOIf taskDAO;
    private SubmissionDAOIf submissionDAO;
    private UserDAOIf userDAO;
    private ParticipationDAOIf participationDAO;
    private String message = "";
    final static private Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());


    public SubmissionService() {
        this.session = HibernateApiSessionManager.getSession();
        this.taskDAO = DAOFactory.TaskDAOIf(session);
        this.submissionDAO = DAOFactory.SubmissionDAOIf(session);
        this.userDAO = DAOFactory.UserDAOIf(session);
        this.participationDAO = DAOFactory.ParticipationDAOIf(session);

    }


    public Response getSubmissionById(int submissionId, int taskId, int userId) {
        Task task = taskDAO.getTask(taskId);

        if (task == null) {
            message = "Aufgabe nicht gefunden";
            ErrorMessage errorMessage = new ErrorMessage(message);
            throw new WebApplicationException(Response.status(Response.Status.NOT_FOUND)
                    .entity(errorMessage).build());
        }

        User user = userDAO.getUser(userId);
        Participation participation = participationDAO.getParticipation(user, task.getTaskGroup().getLecture());

        if (participation == null) {
            message = "Sie versuchen auf eine Veranstaltung zuzugreifen, für die Sie nicht angemeldet sind";
            ErrorMessage errorMessage = new ErrorMessage(message);
            throw new WebApplicationException(Response.status(Response.Status.FORBIDDEN)
                    .entity(errorMessage).build());
        }

        Submission submission = submissionDAO.getSubmission(submissionId);

        if (submission == null) {
            message = "Abgabe nicht gefunden";
            ErrorMessage errorMessage = new ErrorMessage(message);
            throw new WebApplicationException(Response.status(Response.Status.NOT_FOUND)
                    .entity(errorMessage).build());
        }

        return Response
                .ok()
                .entity(submission)
                .build();
    }


    public Response getSubmissions(int participationId) {
        Participation participation = participationDAO.getParticipation(participationId);

        if (participation == null) {
            message = "Nicht gefunden";
            ErrorMessage errorMessage = new ErrorMessage(message);
            throw new WebApplicationException(Response.status(Response.Status.NOT_FOUND)
                    .entity(errorMessage).build());
        }

        List<Submission> submissions = submissionDAO.getAllSubmissions(participation);

        if (submissions.isEmpty()) {
            message = "Sie haben (noch) keine Abgabe angelegt";
            ErrorMessage errorMessage = new ErrorMessage(message);
            throw new WebApplicationException(Response.status(Response.Status.NOT_FOUND)
                    .entity(errorMessage).build());
        }

        return Response
                .ok()
                .entity(submissions)
                .build();

    }


    public Response submitSubmission(int taskId, int userId, HttpServletRequest request) throws IOException, ServletException {
        Task task = taskDAO.getTask(taskId);

        if (task == null) {
            message = "Aufgabe nicht gefunden";
            ErrorMessage errorMessage = new ErrorMessage(message);
            throw new WebApplicationException(Response.status(Response.Status.NOT_FOUND)
                    .entity(errorMessage).build());
        }

        User user = userDAO.getUser(userId);
        Participation participation = participationDAO.getParticipation(user, task.getTaskGroup().getLecture());

        if (participation == null) {
            message = "Sie nehmen an dieser Veranstaltung nicht teil";
            ErrorMessage errorMessage = new ErrorMessage(message);
            throw new WebApplicationException(Response.status(Response.Status.FORBIDDEN)
                    .entity(errorMessage).build());
        }


        Part file = null;
        String contentType = request.getContentType();
        if (contentType.toLowerCase(Locale.ENGLISH).startsWith("multipart/")) {
            file = request.getPart("file");
        }

        if (file != null) {
            if (!request.getParts().stream().allMatch(part -> part.getSize() <= task.getMaxsize())) {
                message = "Datei ist zu groß (maximum sind " + task.getMaxsize() + " Bytes)";
                ErrorMessage errorMessage = new ErrorMessage(message);
                throw new WebApplicationException(Response.status(Response.Status.BAD_REQUEST)
                        .entity(errorMessage).build());
            }
            long fileParts = request.getParts().stream().filter(part -> "file".equals(part.getName())).count();
            if (fileParts > 1 && fileParts != request.getParts().stream().filter(part -> "file".equals(part.getName())).map(part -> Util.getUploadFileName(part)).collect(Collectors.toSet()).size()) {
                message = "Mehrere Dateien mit identischem Namen im Upload gefunden";
                ErrorMessage errorMessage = new ErrorMessage(message);
                throw new WebApplicationException(Response.status(Response.Status.BAD_REQUEST)
                        .entity(errorMessage).build());
            }
        }

        if (task.getStart().after(new Date())) {
            message = "Abgabe nicht gefunden";
            ErrorMessage errorMessage = new ErrorMessage(message);
            throw new WebApplicationException(Response.status(Response.Status.NOT_FOUND)
                    .entity(errorMessage).build());
        }
        if (task.getDeadline().before(new Date())) {
            message = "Abgabe nicht mehr möglich";
            ErrorMessage errorMessage = new ErrorMessage(message);
            throw new WebApplicationException(Response.status(Response.Status.BAD_REQUEST)
                    .entity(errorMessage).build());
        }
        if (file != null && "-".equals(task.getFilenameRegexp())) {
            message = "Dateiupload ist für diese Aufgabe deaktiviert";
            ErrorMessage errorMessage = new ErrorMessage(message);
            throw new WebApplicationException(Response.status(Response.Status.BAD_REQUEST)
                    .entity(errorMessage).build());

        } else if (file == null && !task.isShowTextArea() && !task.isSCMCTask() && !task.isClozeTask()) {
            message = "Textlösungen sind für diese Aufgabe deaktiviert";
            ErrorMessage errorMessage = new ErrorMessage(message);
            throw new WebApplicationException(Response.status(Response.Status.BAD_REQUEST)
                    .entity(errorMessage).build());
        }

        SubmissionDAOIf submissionDAO = DAOFactory.SubmissionDAOIf(session);

        Transaction tx = session.beginTransaction();
        // lock participation (in createSubmission), because locking of not-existing entries in InnoDB might lock the whole table (submissions AND tasks) causing a strict serialization of ALL requests
        Submission submission = submissionDAO.createSubmission(task, participation);

        if (task.isAllowPrematureSubmissionClosing() && submission.isClosed()) {

            message = "Die Abgabe wurde bereits als endgültig abgeschlossen markiert. Eine Veränderung ist daher nicht mehr möglich";
            ErrorMessage errorMessage = new ErrorMessage(message);
            throw new WebApplicationException(Response.status(Response.Status.BAD_REQUEST)
                    .entity(errorMessage).build());
        }

        File taskPath = new File(Configuration.getInstance().getDataPath().getAbsolutePath() + System.getProperty("file.separator") + task.getTaskGroup().getLecture().getId() + System.getProperty("file.separator") + task.getTaskid());
        File path = new File(taskPath, String.valueOf(submission.getSubmissionid()));
        if (!path.exists()) {
            path.mkdirs();
        }

        if (file != null) {
            LogEntry logEntry = new LogDAO(session).createLogUploadEntryTransaction(participation.getUser(), task, LogEntry.LogAction.UPLOAD, null);
            File logPath = new File(taskPath, "logs" + System.getProperty("file.separator") + String.valueOf(logEntry.getId()));
            logPath.mkdirs();
            boolean skippedFiles = false;
            Vector<String> uploadedFilenames = new Vector<>();
            for (Part aFile : request.getParts()) {
                if (!aFile.getName().equalsIgnoreCase("file")) {
                    continue;
                }
                StringBuffer submittedFileName = new StringBuffer(Util.getUploadFileName(aFile));
                Util.lowerCaseExtension(submittedFileName);
                String fileName = null;
                for (Pattern pattern : getTaskFileNamePatterns(task, false)) {
                    Matcher m = pattern.matcher(submittedFileName);
                    if (!m.matches()) {
                        LOG.debug("File does not match pattern: file;" + submittedFileName + ";" + pattern.pattern());
                        skippedFiles = true;
                        fileName = null;
                        break;
                    }
                    fileName = m.group(1);
                }
                if (fileName == null) {
                    continue;
                }
                try {
                    if (!handleUploadedFile(LOG, path, task, fileName, aFile)) {
                        skippedFiles = true;
                    }
                    Util.copyInputStreamAndClose(aFile.getInputStream(), new File(logPath, fileName));
                    uploadedFilenames.add(fileName);
                } catch (IOException | IllegalArgumentException e) {
                    if (!submissionDAO.deleteIfNoFiles(submission, path)) {
                        submission.setLastModified(new Date());
                        submissionDAO.saveSubmission(submission);
                    }
                    LOG.error("Problem on processing uploaded file", e);
                    Util.recursiveDeleteEmptyDirectories(logPath);
                    if (!logPath.exists()) {
                        session.remove(logEntry);
                    } else {
                        logEntry.setAdditionalData(Json.createObjectBuilder().add("filenames", Json.createArrayBuilder(uploadedFilenames)).build().toString());
                        session.update(logEntry);
                    }
                    tx.commit();
                    message = "Problem beim Speichern der Daten";
                    ErrorMessage errorMessage = new ErrorMessage(message);
                    throw new WebApplicationException(Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                            .entity(errorMessage).build());
                }
            }
            if (!submissionDAO.deleteIfNoFiles(submission, path)) {
                submission.setLastModified(new Date());
                submissionDAO.saveSubmission(submission);
            }
            if (!uploadedFilenames.isEmpty()) {
                logEntry.setAdditionalData(Json.createObjectBuilder().add("filenames", Json.createArrayBuilder(uploadedFilenames)).build().toString());
                session.update(logEntry);
            } else {
                session.remove(logEntry);
                Util.recursiveDeleteEmptyDirectories(logPath);
            }
            tx.commit();

            if (skippedFiles) {
                message = "Nicht alle Dateien wurden verarbeitet. ";

                if (uploadedFilenames.isEmpty()) {
                    message += "Es konnte keine Datei verarbeitet werden, da der ";
                } else {
                    message += "Nicht alle Dateien konnten verarbeitet werden, da mindestens ein ";
                }
                message += "Dateiname ungültig war bzw. nicht der Vorgabe entsprach (ist z.B. ein Klassenname vorgegeben, so muss die Datei genauso heißen, Tipp: Nur A-Z, a-z, 0-9, ., - und _ sind erlaubt. Evtl. muss der Dateiname mit einem Großbuchstaben beginnen und darf keine Leerzeichen enthalten).";

                ErrorMessage errorMessage = new ErrorMessage(message);
                throw new WebApplicationException(Response.status(Response.Status.BAD_REQUEST)
                        .entity(errorMessage).build());
            }

            for (Test test : task.getTests()) {
                if (test instanceof UMLConstraintTest && test.getTimesRunnableByStudents() > 0) {
                    break;
                }
            }
        } else {
            if (!submissionDAO.deleteIfNoFiles(submission, path)) {
                submission.setLastModified(new Date());
                submissionDAO.saveSubmission(submission);
            }
            LOG.error("Found no data on upload.");
            tx.commit();

            message = "Keine Abgabedaten gefunden";
            ErrorMessage errorMessage = new ErrorMessage(message);
            throw new WebApplicationException(Response.status(Response.Status.BAD_REQUEST)
                    .entity(errorMessage).build());
        }

        return Response.ok().build();

    }

    public static Vector<Pattern> getTaskFileNamePatterns(Task task, boolean ignoreTaskPattern) {
        Vector<Pattern> patterns = new Vector<>(2);
        patterns.add(Pattern.compile(Configuration.GLOBAL_FILENAME_REGEXP));
        if (!(task.getFilenameRegexp() == null || task.getFilenameRegexp().isEmpty() || ignoreTaskPattern)) {
            patterns.add(Pattern.compile("^(" + task.getFilenameRegexp() + ")$"));
        }
        return patterns;
    }

    private static Vector<Pattern> getArchiveFileNamePatterns(Task task) {
        Vector<Pattern> patterns = new Vector<>(2);
        patterns.add(Pattern.compile(Configuration.GLOBAL_ARCHIVEFILENAME_REGEXP));
        if (task.getArchiveFilenameRegexp() != null && !task.getArchiveFilenameRegexp().isEmpty()) {
            if (task.getArchiveFilenameRegexp().startsWith("^")) {
                patterns.add(Pattern.compile("^(" + task.getArchiveFilenameRegexp().substring(1) + ")$"));
            } else {
                patterns.add(Pattern.compile("(?:^|.*/)(" + task.getArchiveFilenameRegexp() + ")$"));
            }
        }
        return patterns;
    }

    public static boolean handleUploadedFile(Logger log, File submissionPath, Task task, String fileName, Part item) throws IOException {
        if (!"-".equals(task.getArchiveFilenameRegexp()) && (fileName.endsWith(".zip") || fileName.endsWith(".jar"))) {
            boolean skippedFiles = false;
            Vector<Pattern> patterns = getArchiveFileNamePatterns(task);
            try (ZipInputStream zipFile = new ZipInputStream(item.getInputStream(), Configuration.getInstance().getDefaultZipFileCharset())) {
                ZipEntry entry = null;
                while ((entry = zipFile.getNextEntry()) != null) {
                    if (entry.isDirectory()) {
                        continue;
                    }
                    StringBuffer archivedFileName = new StringBuffer(entry.getName().replace("\\", "/"));
                    boolean fileNameOk = true;
                    for (Pattern pattern : patterns) {
                        if (!pattern.matcher(archivedFileName).matches()) {
                            log.debug("Ignored entry: " + archivedFileName + ";" + pattern.pattern());
                            fileNameOk = false;
                            break;
                        }
                    }
                    if (!fileNameOk || archivedFileName.length() == 0 || archivedFileName.charAt(0) == '/' || archivedFileName.charAt(archivedFileName.length() - 1) == '/') {
                        log.debug("Ignored entry: " + archivedFileName);
                        skippedFiles = true;
                        continue;
                    }
                    try {
                        if (!new File(submissionPath, archivedFileName.toString()).getCanonicalPath().startsWith(submissionPath.getCanonicalPath())) {
                            log.debug("Ignored entry: " + archivedFileName + "; tries to escape submissiondir");
                            skippedFiles = true;
                            continue;
                        }
                    } catch (IOException e) {
                        // i.e. filename not valid on system
                        continue;
                    }
                    if (!entry.getName().toLowerCase().endsWith(".class") && !entry.getName().startsWith("__MACOSX/")) {
                        Util.lowerCaseExtension(archivedFileName);
                        // TODO: relocate java-files from jar/zip archives?
                        File fileToCreate = new File(submissionPath, archivedFileName.toString());
                        if (!fileToCreate.getParentFile().exists()) {
                            fileToCreate.getParentFile().mkdirs();
                        }
                        Util.copyInputStreamAndClose(zipFile, fileToCreate);
                    }
                }
            }
            return !skippedFiles;
        }

        Util.saveAndRelocateJavaFile(item, submissionPath, fileName);
        return true;
    }
}
