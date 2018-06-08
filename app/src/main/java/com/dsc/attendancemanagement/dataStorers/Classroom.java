package com.dsc.attendancemanagement.dataStorers;

import java.io.Serializable;

/**
 * Created by Developer Student Club
 * Varshit Ratna(lead)
 * Devaraj Akhil(Core team)
 */
public class Classroom implements Serializable {
    private int id;
    private String name;
    private int studentNumber;

    public Classroom() {
        id = 0;
        name = "";
        studentNumber = 0;
    }

    public Classroom(String name) {
        id = 0;
        this.name = name;
        studentNumber = 0;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getStudentNumber() {
        return studentNumber;
    }

    public void setStudentNumber(int studentNumber) {
        this.studentNumber = studentNumber;
    }
}
