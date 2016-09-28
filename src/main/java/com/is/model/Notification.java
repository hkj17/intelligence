package com.is.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;


@Entity(name = "Notification")
@Table(name = "notification")
public class Notification {
	
	
	@Id
	@Column(name = "note_id")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int noteId;
	
	@Column(name = "note_time")
	private String noteTime;
	
	@Column(name = "note_text")
	private String noteText;
	
	@Column(name = "note_title")
	private String noteTitle;
	
	@Column(name = "note_author")
	private String noteAuthor;
	
	
	public String getNoteTitle() {
		return noteTitle;
	}
	public void setNoteTitle(String noteTitle) {
		this.noteTitle = noteTitle;
	}
	public String getNoteAuthor() {
		return noteAuthor;
	}
	public void setNoteAuthor(String noteAuthor) {
		this.noteAuthor = noteAuthor;
	}
	public int getNoteId() {
		return noteId;
	}
	public void setNoteId(int noteId) {
		this.noteId = noteId;
	}
	public String getNoteTime() {
		return noteTime;
	}
	public void setNoteTime(String noteTime) {
		this.noteTime = noteTime;
	}
	public String getNoteText() {
		return noteText;
	}
	public void setNoteText(String noteText) {
		this.noteText = noteText;
	}
	
	

}
