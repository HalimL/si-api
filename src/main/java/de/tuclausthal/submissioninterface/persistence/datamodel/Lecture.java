/*
 * Copyright 2009-2012, 2020-2021 Sven Strickroth <email@cs-ware.de>
 * 
 * This file is part of the SubmissionInterface.
 * 
 * SubmissionInterface is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License version 3 as
 * published by the Free Software Foundation.
 * 
 * SubmissionInterface is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with SubmissionInterface. If not, see <http://www.gnu.org/licenses/>.
 */

package de.tuclausthal.submissioninterface.persistence.datamodel;

import java.io.Serializable;
import java.util.List;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.hibernate.annotations.OrderBy;

@Entity
@Table(name = "lectures")
public class Lecture implements Serializable {
	private static final long serialVersionUID = 1L;

	private int id;
	private String name;
	private int semester;
	private boolean requiresAbhnahme;
	private Set<Participation> participants;
	private List<TaskGroup> taskGroups;
	private Set<Group> groups;
	private String gradingMethod = "";
	private String description = "";
	private boolean allowSelfSubscribe = true;

	/**
	 * @return the name
	 */
	@Column(nullable = false)
	public String getName() {
		return name;
	}

	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @return the semester
	 */
	public int getSemester() {
		return semester;
	}

	/**
	 * @param semester the semester to set
	 */
	public void setSemester(int semester) {
		this.semester = semester;
	}

	/**
	 * 
	 * @return the human-readable semester
	 */
	@Transient
	public String getReadableSemester() {
		String semester = ((Integer) getSemester()).toString();
		if (getSemester() % 2 != 0) {
			return "WS " + semester.substring(0, 4) + "/" + ((getSemester() - 1) / 10 + 1);
		}
		return "SS " + semester.substring(0, 4);
	}

	/**
	 * @return the participants
	 */
	@OneToMany(mappedBy = "lecture", fetch = FetchType.LAZY)
	@OnDelete(action = OnDeleteAction.CASCADE)
	@JsonIgnore
	public Set<Participation> getParticipants() {
		return participants;
	}

	/**
	 * @param participants the participants to set
	 */
	public void setParticipants(Set<Participation> participants) {
		this.participants = participants;
	}

	/**
	 * @return the id
	 */
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	public int getId() {
		return id;
	}

	/**
	 * @param id the id to set
	 */
	public void setId(int id) {
		this.id = id;
	}

	/**
	 * @return the taskGroups
	 */
	@OneToMany(mappedBy = "lecture", fetch = FetchType.LAZY)
	@OnDelete(action = OnDeleteAction.CASCADE)
	@OrderBy(clause = "taskgroupid asc")
	@JsonIgnore
	public List<TaskGroup> getTaskGroups() {
		return taskGroups;
	}

	/**
	 * @param taskGroups the taskGroups to set
	 */
	public void setTaskGroups(List<TaskGroup> taskGroups) {
		this.taskGroups = taskGroups;
	}

	/**
	 * @return the groups
	 */
	@OneToMany(mappedBy = "lecture", fetch = FetchType.LAZY)
	@OnDelete(action = OnDeleteAction.CASCADE)
	@OrderBy(clause = "name asc")
	@JsonIgnore
	public Set<Group> getGroups() {
		return groups;
	}

	/**
	 * @param groups the groups to set
	 */
	public void setGroups(Set<Group> groups) {
		this.groups = groups;
	}

	/**
	 * @return the requiresAbhnahme
	 */
	public boolean isRequiresAbhnahme() {
		return requiresAbhnahme;
	}

	/**
	 * @param requiresAbhnahme the requiresAbhnahme to set
	 */
	public void setRequiresAbhnahme(boolean requiresAbhnahme) {
		this.requiresAbhnahme = requiresAbhnahme;
	}

	/**
	 * @return the gradingMethod
	 */
	@Column(nullable = false)
	public String getGradingMethod() {
		return gradingMethod;
	}

	/**
	 * @param gradingMethod the gradingMethod to set
	 */
	public void setGradingMethod(String gradingMethod) {
		this.gradingMethod = gradingMethod;
	}

	/**
	 * @return the description
	 */
	@Column(nullable = false, columnDefinition="TEXT")
	public String getDescription() {
		return description;
	}

	/**
	 * @param description the description to set
	 */
	public void setDescription(String description) {
		this.description = description;
	}

	/**
	 * @return the allowSelfSubscribe
	 */
	public boolean isAllowSelfSubscribe() {
		return allowSelfSubscribe;
	}

	/**
	 * @param allowSelfSubscribe the allowSelfSubscribe to set
	 */
	public void setAllowSelfSubscribe(boolean allowSelfSubscribe) {
		this.allowSelfSubscribe = allowSelfSubscribe;
	}
}