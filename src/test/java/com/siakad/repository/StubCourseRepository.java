package com.siakad.repository;

import com.siakad.model.Course;
import java.util.*;

public class StubCourseRepository implements CourseRepository {
    private Map<String, Course> courses = new HashMap<>();
    private Map<String, Boolean> prerequisiteResults = new HashMap<>();

    @Override
    public Course findByCourseCode(String courseCode) {
        return courses.get(courseCode);
    }

    @Override
    public void update(Course course) {
        courses.put(course.getCourseCode(), course);
    }

    @Override
    public boolean isPrerequisiteMet(String studentId, String courseCode) {
        String key = studentId + "-" + courseCode;
        return prerequisiteResults.getOrDefault(key, true);
    }

    // Helper methods
    public void addCourse(Course course) {
        courses.put(course.getCourseCode(), course);
    }

    public void setPrerequisiteResult(String studentId, String courseCode, boolean result) {
        String key = studentId + "-" + courseCode;
        prerequisiteResults.put(key, result);
    }
}