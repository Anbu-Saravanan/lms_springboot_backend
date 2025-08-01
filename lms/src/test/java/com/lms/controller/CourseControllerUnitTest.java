//package com.lms.controller;
//
//import com.lms.requestDTO.CourseRequestDTO;
//import com.lms.responseDTO.CourseResponseDTO;
//import com.lms.service.CourseService;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.mockito.Mockito;
//import org.springframework.http.ResponseEntity;
//
//import java.util.Arrays;
//import java.util.List;
//
//import static org.junit.jupiter.api.Assertions.*;
//import static org.mockito.ArgumentMatchers.any;
//
//class CourseControllerUnitTest {
//
//    private CourseController courseController;
//    private CourseService courseService;
//
//    @BeforeEach
//    void setUp() {
//        courseService = Mockito.mock(CourseService.class);
//        courseController = new CourseController();
//        // Use reflection or constructor injection as needed
//        // For @Autowired field:
//        var field = CourseController.class.getDeclaredFields()[0]; // gets first field (courseService)
//        field.setAccessible(true);
//        try { field.set(courseController, courseService); } catch (Exception e) { throw new RuntimeException(e); }
//    }
//
//    @Test
//    void testCreateCourse() {
//        CourseRequestDTO req = new CourseRequestDTO();
//        req.setTitle("Java Basics");
//        CourseResponseDTO resp = new CourseResponseDTO();
//        resp.setTitle("Java Basics");
//        Mockito.when(courseService.createCourse(any(CourseRequestDTO.class))).thenReturn(resp);
//
//        ResponseEntity<CourseResponseDTO> response = courseController.createCourse(req);
//
//        assertEquals("Java Basics", response.getBody().getTitle());
//    }
//
//    @Test
//    void testGetAllCourses() {
//        CourseResponseDTO resp = new CourseResponseDTO();
//        resp.setTitle("Java Basics");
//        Mockito.when(courseService.getAllCourses()).thenReturn(Arrays.asList(resp));
//
//        ResponseEntity<List<CourseResponseDTO>> response = courseController.getAllCourses();
//
//        assertEquals(1, response.getBody().size());
//        assertEquals("Java Basics", response.getBody().get(0).getTitle());
//    }
//}
