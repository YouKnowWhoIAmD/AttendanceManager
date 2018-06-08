package com.dsc.attendancemanagement.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;

import com.dsc.attendancemanagement.dataStorers.Attendance;
import com.dsc.attendancemanagement.dataStorers.Classroom;
import com.dsc.attendancemanagement.dataStorers.Student;

/**
 * Created by Developer Student Club
 * Varshit Ratna(lead)
 * Devaraj Akhil(Core team)
 */
public class DatabaseManager extends SQLiteOpenHelper {
    //The Android's default_flag system path of your application database.
    private static final String DB_NAME = "ClassroomManager";
    private static final int VERSION = 3;

    private static final String CREATE_TABLE_STUDENT = "CREATE TABLE `Student` (\n" +
            "\t`id`\tINTEGER PRIMARY KEY AUTOINCREMENT,\n" +
            "\t`rollno`\tTEXT,\n" +
            "\t`name`\tTEXT\n" +
                        ");";

    private static final String CREATE_TABLE_CLASSROOM = "CREATE TABLE `Classroom` (\n" +
            "\t`id`\tINTEGER PRIMARY KEY AUTOINCREMENT,\n" +
            "\t`name`\tTEXT\n" +
            ");";

    private static final String CREATE_TABLE_CLASSROOMSTUDENT = "CREATE TABLE `ClassroomStudent` (\n" +
            "\t`id`\tINTEGER PRIMARY KEY AUTOINCREMENT,\n" +
            "\t`classroom_id`\tINTEGER,\n" +
            "\t`student_id`\tINTEGER\n" +
            ");";

    private static final String CREATE_TABLE_ATTENDANCE = "CREATE TABLE `Attendance` (\n" +
            "\t`id`\tINTEGER PRIMARY KEY AUTOINCREMENT,\n" +
            "\t`date_time`\tTEXT,\n" +
            "\t`present`\tINTEGER,\n" +
            "\t`classroomstudent_id`\tINTEGER\n" +
            ");";

    public DatabaseManager(Context context) {
        super(context, DB_NAME, null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE_STUDENT);
        db.execSQL(CREATE_TABLE_CLASSROOM);
        db.execSQL(CREATE_TABLE_CLASSROOMSTUDENT);
        db.execSQL(CREATE_TABLE_ATTENDANCE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS Student");
        db.execSQL("DROP TABLE IF EXISTS Classroom");
        db.execSQL("DROP TABLE IF EXISTS ClassroomStudent");
        db.execSQL("DROP TABLE IF EXISTS Attendance");

        onCreate(db);
    }

    /**
     * Select classrooms
     * @return
     */
    public ArrayList<Classroom> selectClassrooms() {
        SQLiteDatabase db = this.getReadableDatabase();

        ArrayList<Classroom> list = new ArrayList<Classroom>();

        String query = "SELECT id, name FROM classroom";
        Cursor cursor = db.rawQuery(query, null);

        if (cursor.moveToFirst()) {
            do {
                Classroom classroom = new Classroom();
                classroom.setId(cursor.getInt(0));
                classroom.setName(cursor.getString(1));

                list.add(classroom);
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();

        return list;
    }

    /**
     * Select classrooms with student number
     * @return
     */
    public ArrayList<Classroom> selectClassroomsWithStudentNumber() {
        SQLiteDatabase db = this.getReadableDatabase();

        ArrayList<Classroom> list = new ArrayList<Classroom>();

        String query = "SELECT classroom.id, classroom.name, COUNT(classroomStudent.student_id) " +
                "FROM classroom " +
                "INNER JOIN classroomStudent " +
                "ON classroom.id = classroomStudent.classroom_id " +
                "GROUP BY classroom.id";
        Cursor cursor = db.rawQuery(query, null);

        if (cursor.moveToFirst()) {
            do {
                Classroom classroom = new Classroom();
                classroom.setId(cursor.getInt(0));
                classroom.setName(cursor.getString(1));
                classroom.setStudentNumber(cursor.getInt(2));

                list.add(classroom);
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();

        return list;
    }

    /**
     * Insert into classroom
     * @param name
     */
    public boolean insertClassroom(String name) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put("name", name);
        boolean isSuccessful = db.insert("classroom", null, values) > 0;

        db.close();

        return isSuccessful;
    }

    /**
     * Delete classroom item
     * @param classroomId
     * @return
     */
    public boolean deleteClassroom(int classroomId) {
        String classroom_id = String.valueOf(classroomId);

        deleteAttendanceRowsForClassroom(classroom_id);
        deleteClassroomStudentRowsForClassroom(classroom_id);

        SQLiteDatabase db = this.getWritableDatabase();

        boolean isSuccessful = db.delete("classroom", "id = ?", new String[]{classroom_id}) > 0;

        db.close();

        return isSuccessful;
    }

    /**
     * Delete attendance table rows that are related with given classroom.<br />
     * Use this method with deleteClassroom method
     * @param classroom_id
     */
    private void deleteAttendanceRowsForClassroom(String classroom_id) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete("attendance", "attendance.classroomstudent_id IN " +
                        "(SELECT classroomStudent.id FROM classroomStudent " +
                        "WHERE classroomStudent.classroom_id = ?)",
                new String[]{classroom_id});

        db.close();
    }

    /**
     * Delete classroomstudent table rows that are related with given classroom.<br />
     * Use this method with deleteClassroom method
     * @param classroom_id
     */
    private void deleteClassroomStudentRowsForClassroom(String classroom_id) {
        SQLiteDatabase db = this.getWritableDatabase();

        db.delete("classroomStudent", "classroom_id = ?",
                new String[]{classroom_id});

        db.close();
    }

    /**
     * Select students
     * @param classroomId
     * @return
     */
    public ArrayList<Student> selectStudents(int classroomId) {
        SQLiteDatabase db = this.getReadableDatabase();

        String classroom_id = String.valueOf(classroomId);
        ArrayList<Student> list = new ArrayList<Student>();

        String query = "SELECT student.id, student.rollno,student.name, classroomstudent.id FROM student " +
                "INNER JOIN classroomstudent " +
                "ON student.id = classroomstudent.student_id " +
                "WHERE classroomstudent.classroom_id = ?";
        Cursor cursor = db.rawQuery(query, new String[]{classroom_id});

        if (cursor.moveToFirst()) {
            do {
                Student student = new Student();
                student.setId(cursor.getInt(0));
                student.setRollNo(cursor.getString(1));
                student.setName(cursor.getString(2));
                student.setClassroomStudentId(cursor.getInt(3));
                list.add(student);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();

        return list;
    }

    /**
     * Insert into student
     * @param classroomId
     * @param name
     */
    public boolean insertStudent(int classroomId, String rollno, String name) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put("name", name);
        values.put("rollno", rollno);
        boolean isSuccessful = db.insert("student", null, values) > 0;

        db.close();

        int studentId = selectLastStudentId();
        boolean isSuccessfulClassromStudent = insertClassroomStudent(classroomId, studentId);

        return isSuccessful && isSuccessfulClassromStudent;
    }

    /**
     * ClassroomStudent table
     * @param classroomId
     * @param studentId
     * @return
     */
    private boolean insertClassroomStudent(int classroomId, int studentId) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put("classroom_id", classroomId);
        values.put("student_id", studentId);
        boolean isSuccessful = db.insert("classroomstudent", null, values) > 0;

        db.close();

        return isSuccessful;
    }

    /**
     * Retrieve last student id
     * @return
     */
    private int selectLastStudentId() {
        SQLiteDatabase db = this.getReadableDatabase();

        int lastStudentId = 0;
        String query = "SELECT MAX(id) FROM student";
        Cursor cursor = db.rawQuery(query, null);

        if (cursor.moveToFirst()) {
            lastStudentId = cursor.getInt(0);
        }

        cursor.close();
        db.close();

        return lastStudentId;
    }

    /**
     * Delete student item
     * @param studentId
     * @param classroomId
     * @return
     */
    public boolean deleteStudent(int studentId, int classroomId) {
        String student_id = String.valueOf(studentId);
        String classroom_id = String.valueOf(classroomId);

        deleteAttendanceRowsForStudent(student_id, classroom_id);
        deleteClassroomStudentRowsForStudent(student_id, classroom_id);

        SQLiteDatabase db = this.getWritableDatabase();

        boolean isSuccessful = db.delete("student", "id = ?", new String[]{student_id}) > 0;

        db.close();

        return isSuccessful;
    }

    /**
     * Delete attendance table rows that are related with given student and classroom.<br />
     * Use this method with deleteStudent method
     * @param student_id
     * @param classroom_id
     */
    private void deleteAttendanceRowsForStudent(String student_id, String classroom_id) {
        SQLiteDatabase db = this.getWritableDatabase();

        db.delete("attendance", "attendance.classroomstudent_id IN " +
                        "(SELECT classroomStudent.id FROM classroomStudent " +
                        "WHERE classroomStudent.classroom_id = ? " +
                        "AND classroomStudent.student_id = ?)",
                new String[]{classroom_id, student_id});

        db.close();
    }

    /**
     * Delete classroomstudent table rows that are related with given student and classroom.<br />
     * Call this method with deleteStudent
     * @param student_id
     * @param classroom_id
     * @return
     */
    public boolean deleteClassroomStudentRowsForStudent(String student_id, String classroom_id) {
        SQLiteDatabase db = this.getWritableDatabase();

        boolean isSuccessful = db.delete("classroomstudent", "student_id = ?"
               + " and classroom_id = ?", new String[]{student_id, classroom_id}) > 0;

        db.close();

        return isSuccessful;
    }

    /**
     * Insert into attendance
     * @param students
     * @param date_time
     * @return
     */
    public boolean insertAttendance(ArrayList<Student> students, String date_time) {
        SQLiteDatabase db = this.getWritableDatabase();

        //it is used to check if all rows are successfully inserted
        int numberOfSuccessfulInsert = 0;

        for (Student student : students) {
            ContentValues values = new ContentValues();
            values.put("date_time", date_time);
            int present = student.isPresent() == true ? 1 : 0;
            values.put("present", present);
            values.put("classroomstudent_id", student.getClassroomStudentId());
            boolean isSuccessful = db.insert("attendance", null, values) > 0;

            if (isSuccessful)
                numberOfSuccessfulInsert++;
        }

        db.close();

        //if all are ok, send true
        //if even one is not ok, send false
        return students.size() == numberOfSuccessfulInsert;
    }

    /**
     * Check attendance whether already exists
     * @param classroomId
     * @param date_time
     * @return
     */
    public boolean selectAttendanceToCheckExistance(int classroomId, String date_time) {
        SQLiteDatabase db = this.getReadableDatabase();

        String classroom_id = String.valueOf(classroomId);
        ArrayList<Attendance> list = new ArrayList<Attendance>();

        String query = "SELECT distinct date_time FROM attendance " +
                "WHERE classroomstudent_id in (SELECT id FROM classroomStudent " +
                "WHERE classroom_id = ?) AND date_time = ?";
        Cursor cursor = db.rawQuery(query, new String[]{classroom_id, date_time});

        boolean isExist = false;
        if (cursor.moveToFirst()) {
            isExist = true;
        }

        cursor.close();
        db.close();

        return isExist;
    }

    /**
     * All attendances for excel
     * @return
     */
    public ArrayList<Attendance> selectAllAttendances() {
        SQLiteDatabase db = this.getReadableDatabase();

        ArrayList<Attendance> list = new ArrayList<Attendance>();

        String query = "SELECT attendance.date_time, attendance.present, " +
                "student.name, classroom.name, " +
                "classroomStudent.student_id, classroomStudent.classroom_id " +
                "FROM student, classroom " +
                "INNER JOIN classroomStudent " +
                "ON student.id = classroomStudent.student_id " +
                "INNER JOIN attendance " +
                "ON classroomStudent.id = attendance.classroomstudent_id " +
                "WHERE classroom.id = classroomStudent.classroom_id " +
                "ORDER BY classroomStudent.classroom_id, classroomStudent.student_id";

        Cursor cursor = db.rawQuery(query, null);

        if (cursor.moveToFirst()) {
            do {
                Attendance attendance = new Attendance();

                attendance.setDateTime(cursor.getString(0));
                attendance.setPresent(cursor.getInt(1));
                attendance.setStudentName(cursor.getString(2));
                attendance.setClassroomName(cursor.getString(3));
                attendance.setStudentId(cursor.getInt(4));
                attendance.setClassroomId(cursor.getInt(5));

                list.add(attendance);
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();

        if (list != null)
            Collections.sort(list, new DateComparator());

        return list;
    }

    /**
     * Given class' all attendances
     * @param classroomId
     * @return
     */
    public ArrayList<Attendance> selectAllAttendancesOfClass(int classroomId) {
        SQLiteDatabase db = this.getReadableDatabase();

        String classroom_id = String.valueOf(classroomId);
        ArrayList<Attendance> list = new ArrayList<Attendance>();

        String query = "SELECT attendance.id, attendance.date_time, attendance.present, " +
                "student.name, classroom.name, " +
                "classroomStudent.student_id, classroomStudent.classroom_id, " +
                "(SUM(attendance.present)*100/COUNT(attendance.id)) as presence " +
                "FROM student, classroom " +
                "INNER JOIN classroomStudent " +
                "ON student.id = classroomStudent.student_id " +
                "INNER JOIN attendance " +
                "ON classroomStudent.id = attendance.classroomstudent_id " +
                "WHERE classroom.id = classroomStudent.classroom_id " +
                "AND classroom.id = ? " +
                "GROUP BY classroomStudent.student_id";

        Cursor cursor = db.rawQuery(query, new String[]{classroom_id});

        if (cursor.moveToFirst()) {
            do {
                Attendance attendance = new Attendance();

                attendance.setId(cursor.getInt(0));
                attendance.setDateTime(cursor.getString(1));
                attendance.setPresent(cursor.getInt(2));
                attendance.setStudentName(cursor.getString(3));
                attendance.setClassroomName(cursor.getString(4));
                attendance.setStudentId(cursor.getInt(5));
                attendance.setClassroomId(cursor.getInt(6));
                attendance.setPresencePercentage(cursor.getInt(7));

                list.add(attendance);
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();

        return list;
    }

    /**
     * Given student's all attendances in given class'
     * @param classroomId
     * @return
     */
    public ArrayList<Attendance> selectAllAttendancesOfStudent(int classroomId, int studentId) {
        SQLiteDatabase db = this.getReadableDatabase();

        String classroom_id = String.valueOf(classroomId);
        String student_id = String.valueOf(studentId);
        ArrayList<Attendance> list = new ArrayList<Attendance>();

        String query = "SELECT attendance.id, attendance.date_time, attendance.present, " +
                "student.name, classroom.name, " +
                "classroomStudent.student_id, classroomStudent.classroom_id " +
                "FROM student, classroom " +
                "INNER JOIN classroomStudent " +
                "ON student.id = classroomStudent.student_id " +
                "INNER JOIN attendance " +
                "ON classroomStudent.id = attendance.classroomstudent_id " +
                "WHERE classroom.id = classroomStudent.classroom_id " +
                "AND classroom.id = ? AND classroomStudent.student_id = ? " +
                "ORDER BY classroomStudent.student_id";

        Cursor cursor = db.rawQuery(query, new String[]{classroom_id, student_id});

        if (cursor.moveToFirst()) {
            do {
                Attendance attendance = new Attendance();

                attendance.setId(cursor.getInt(0));
                attendance.setDateTime(cursor.getString(1));
                attendance.setPresent(cursor.getInt(2));
                attendance.setStudentName(cursor.getString(3));
                attendance.setClassroomName(cursor.getString(4));
                attendance.setStudentId(cursor.getInt(5));
                attendance.setClassroomId(cursor.getInt(6));

                list.add(attendance);
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();

        return list;
    }

    public class DateComparator implements Comparator<Attendance> {

        @Override
        public int compare(Attendance a1, Attendance a2) {
            SimpleDateFormat format = new SimpleDateFormat("dd.MM.yyyy HH:mm");
            Date date1 = null;
            Date date2 = null;

            try {
                date1 = format.parse(a1.getDateTime());
                date2 = format.parse(a2.getDateTime());
            } catch (ParseException e) {
                e.printStackTrace();
            }

            if (date1 == null || date2 == null)
                return 0;
            else
                return date1.compareTo(date2);
        }
    }
}