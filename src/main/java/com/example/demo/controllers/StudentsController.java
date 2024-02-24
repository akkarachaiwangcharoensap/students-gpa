package com.example.demo.controllers;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.view.RedirectView;

import com.example.demo.models.Student;
import com.example.demo.models.StudentRepository;

import jakarta.servlet.http.HttpServletResponse;

/**
 * Controller class for handling student-related operations.
 */
@Controller
public class StudentsController {

    @Autowired
    private StudentRepository studentRepository;

    /**
     * Redirects to the students page.
     *
     * @return RedirectView object for redirecting to the students page
     */
    @GetMapping("/")
    public RedirectView process() {
        return new RedirectView("students");
    }

    /**
     * Displays all students.
     *
     * @param model Model object for managing attributes
     * @return String representing the view name for displaying all students
     */
    @GetMapping("/students")
    public String showAllStudents(Model model) {
        List<Student> students = this.studentRepository.findAll();
        model.addAttribute("students", students);
        return "students/all";
    }

    /**
     * Displays the form for creating a new student.
     *
     * @param model Model object for managing attributes
     * @return String representing the view name for creating a new student
     */
    @GetMapping("/students/create")
    public String createStudent(Model model) {
        model.addAttribute("student", new Student());
        return "students/create";
    }

    /**
     * Creates a new student.
     *
     * @param student  Student object representing the new student
     * @param response HttpServletResponse object for managing HTTP response
     * @return String representing the redirection URL after creating the student
     */
    @PostMapping("/students/create")
    public String createStudent(@ModelAttribute Student student, HttpServletResponse response) {

        Student newStudent = new Student();
        newStudent.setName(student.getName());
        newStudent.setGpa(student.getGpa());
        newStudent.setAttending(student.isAttending());
        newStudent.setEnrollmentDate(student.getEnrollmentDate());

        // Set created_at and updated_at timestamps
        Date now = new Date();
        newStudent.setCreatedAt(now);
        newStudent.setUpdatedAt(now);

        this.studentRepository.save(newStudent);
        response.setStatus(HttpServletResponse.SC_CREATED);

        return "redirect:/students";
    }

    /**
     * Displays the form for editing a student.
     *
     * @param id    Integer representing the ID of the student to edit
     * @param model Model object for managing attributes
     * @return String representing the view name for editing a student
     */
    @GetMapping("/students/{id}/edit")
    public String editStudent(@PathVariable("id") Integer id, Model model) {

        Optional<Student> student = this.studentRepository.findById(id);
        if (!student.isPresent()) {
            return "error/404";
        }

        model.addAttribute("student", student);
        return "students/edit";
    }

    /**
     * Updates an existing student.
     *
     * @param id       Integer representing the ID of the student to update
     * @param student  Student object representing the updated student information
     * @param response HttpServletResponse object for managing HTTP response
     * @return String representing the redirection URL after updating the student
     */
    @PostMapping("/students/{id}/update")
    public String updateStudent(@PathVariable("id") Integer id, @ModelAttribute Student student,
            HttpServletResponse response) {

        Optional<Student> studentOptional = this.studentRepository.findById(id);
        if (!studentOptional.isPresent()) {
            return "error/404";
        }

        Student existingStudent = studentOptional.get();
        existingStudent.setName(student.getName());
        existingStudent.setGpa(student.getGpa());
        existingStudent.setAttending(student.isAttending());
        existingStudent.setEnrollmentDate(student.getEnrollmentDate());

        // Set updated_at timestamp
        Date now = new Date();
        existingStudent.setUpdatedAt(now);

        this.studentRepository.save(existingStudent);
        response.setStatus(HttpServletResponse.SC_CREATED);

        return "redirect:/students";
    }

    /**
     * Deletes a student.
     *
     * @param id       Integer representing the ID of the student to delete
     * @param response HttpServletResponse object for managing HTTP response
     * @return String representing the redirection URL after deleting the student
     */
    @DeleteMapping("/students/{id}/delete")
    public String deleteStudent(@PathVariable("id") Integer id, HttpServletResponse response) {
        Optional<Student> studentOptional = studentRepository.findById(id);

        if (!studentOptional.isPresent()) {
            return "error/404";
        }

        studentRepository.delete(studentOptional.get());
        return "redirect:/students";
    }
}
