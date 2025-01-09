package il.cshaifasweng.OCSFMediatorExample.server;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.Timer;

import javax.persistence.NoResultException;
import javax.persistence.Query;

import il.cshaifasweng.OCSFMediatorExample.entities.*;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.hibernate.*;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;
import org.hibernate.service.ServiceRegistry;


/**
 * Hello world!
 *
 */
public class App extends Application {

    private static SimpleServer server;
    private static Session session;
    private static final SessionFactory sessionFactory = getSessionFactory();
    private static List<ScheduledTest> scheduledTests;

    private static Scene scene;
    private static Stage stage;

    public static Scene getScene() {
        return scene;
    }

    public static void setScene(Scene scene) {
        App.scene = scene;
    }

    public static Stage getStage() {
        return stage;
    }

    public static void setStage(Stage stage) {
        App.stage = stage;
    }

    private static Parent loadFXML(String fxml) throws IOException {
        FXMLLoader fxmlLoader = null;
        try {
            fxmlLoader = new FXMLLoader(App.class.getResource(fxml + ".fxml"));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return fxmlLoader.load();
    }


    @Override
    public void start(Stage stage) throws IOException {
        try {

            System.out.println("in start server");
            scene = new Scene(loadFXML("serverControl"), 956, 578);
            stage.setScene(scene);
            stage.setTitle("Server Control");
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }

        new Thread(() -> {
            try {
                server = new SimpleServer(3028);
                server.listen();
                System.out.println("Server is listening to port " + server.getPort());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();


        try {
            session = sessionFactory.openSession();
            session.beginTransaction();
//            generateObjects();

            session.getTransaction().commit(); // Save Everything in the transaction area

        } catch (Exception exception) {
            if (session != null) {
                session.getTransaction().rollback();
            }
            System.err.println("An error occurred, changes have been rolled back.");
            exception.printStackTrace();
        } finally {
            session.close();
        }
    }

    @Override
    public void stop() throws Exception {
        // TODO Auto-generated method stub
        System.out.println("SERVER SHUT DOWN");
        super.stop();
    }

    public static void main(String[] args) throws Exception {
        launch();
    }


    public static Session getSession() {
        return session;
    }

    private static SessionFactory getSessionFactory() throws HibernateException {
        Configuration configuration = new Configuration();
        configuration.addAnnotatedClass(User.class);
        configuration.addAnnotatedClass(Course.class);
        configuration.addAnnotatedClass(ExamForm.class);
        configuration.addAnnotatedClass(Principal.class);
        configuration.addAnnotatedClass(Question.class);
        configuration.addAnnotatedClass(Student.class);
        configuration.addAnnotatedClass(ScheduledTest.class);
        configuration.addAnnotatedClass(StudentTest.class);
        configuration.addAnnotatedClass(Subject.class);
        configuration.addAnnotatedClass(Teacher.class);
        configuration.addAnnotatedClass(Question_Score.class);
        configuration.addAnnotatedClass(Question_Answer.class);
        configuration.addAnnotatedClass(ExtraTime.class);
        configuration.addAnnotatedClass(TestFile.class);

        ServiceRegistry serviceRegistry = new StandardServiceRegistryBuilder()
                .applySettings(configuration.getProperties())
                .build();

        return configuration.buildSessionFactory(serviceRegistry);
    }

    private static void generateObjects() throws Exception {
        System.out.println("Generating objects...");
        List<Student> students = Student.GenerateStudents();
        List<Subject> subjects = Subject.GenerateSubjects();
        List<Course> courses = Course.GenerateCourses();
        List<Teacher> teachers = Teacher.GenerateTeachers();

        Principal principal = new Principal("300", "Oren", "Polishuk", "male", "PoliOren123@gmail.com", "manager");
        session.save(principal);
        session.flush();

// Update Courses
        courses.get(0).setSubject(subjects.get(0));
        courses.get(1).setSubject(subjects.get(0));
        courses.get(2).setSubject(subjects.get(0));
        courses.get(3).setSubject(subjects.get(0));

        subjects.get(0).addCourse(courses.get(0));
        subjects.get(0).addCourse(courses.get(1));
        subjects.get(0).addCourse(courses.get(2));
        subjects.get(0).addCourse(courses.get(3));


        courses.get(4).setSubject(subjects.get(1));
        courses.get(5).setSubject(subjects.get(1));
        courses.get(6).setSubject(subjects.get(1));

        subjects.get(1).addCourse(courses.get(4));
        subjects.get(1).addCourse(courses.get(5));
        subjects.get(1).addCourse(courses.get(6));

        courses.get(7).setSubject(subjects.get(2));
        courses.get(8).setSubject(subjects.get(2));
        courses.get(9).setSubject(subjects.get(2));

        subjects.get(2).addCourse(courses.get(7));
        subjects.get(2).addCourse(courses.get(8));
        subjects.get(2).addCourse(courses.get(9));

//Update Teachers

        teachers.get(0).addCourses(courses.get(0));
        teachers.get(0).addCourses(courses.get(1));
        teachers.get(0).addCourses(courses.get(2));
        teachers.get(0).addCourses(courses.get(3));

        teachers.get(0).addSubject(subjects.get(0));

        courses.get(0).addTeacher(teachers.get(0));
        courses.get(1).addTeacher(teachers.get(0));
        courses.get(2).addTeacher(teachers.get(0));
        courses.get(3).addTeacher(teachers.get(0));

        subjects.get(0).addTeacher(teachers.get(0));

        teachers.get(1).addCourses(courses.get(0));
        teachers.get(1).addCourses(courses.get(1));
        teachers.get(1).addCourses(courses.get(2));
        teachers.get(1).addCourses(courses.get(3));
        teachers.get(1).addCourses(courses.get(4));
        teachers.get(1).addCourses(courses.get(5));
        teachers.get(1).addCourses(courses.get(6));

        teachers.get(1).addSubject(subjects.get(0));
        teachers.get(1).addSubject(subjects.get(1));

        courses.get(0).addTeacher(teachers.get(1));
        courses.get(1).addTeacher(teachers.get(1));
        courses.get(2).addTeacher(teachers.get(1));
        courses.get(3).addTeacher(teachers.get(1));
        courses.get(4).addTeacher(teachers.get(1));
        courses.get(5).addTeacher(teachers.get(1));
        courses.get(6).addTeacher(teachers.get(1));

        subjects.get(1).addTeacher(teachers.get(1));
        subjects.get(2).addTeacher(teachers.get(1));


        teachers.get(2).addCourses(courses.get(7));
        teachers.get(2).addCourses(courses.get(8));
        teachers.get(2).addCourses(courses.get(4));
        teachers.get(2).addCourses(courses.get(5));

        teachers.get(2).addSubject(subjects.get(0));
        teachers.get(2).addSubject(subjects.get(1));

        courses.get(7).addTeacher(teachers.get(2));
        courses.get(8).addTeacher(teachers.get(2));
        courses.get(4).addTeacher(teachers.get(2));
        courses.get(5).addTeacher(teachers.get(2));

        subjects.get(0).addTeacher(teachers.get(2));
        subjects.get(1).addTeacher(teachers.get(2));




// ------------ Add objects to DB --------//

        for (Subject subject : subjects) {
            session.saveOrUpdate(subject);
        }
        for (Course course : courses)
            session.saveOrUpdate(course);
        for (Teacher teacher : teachers)
            session.saveOrUpdate(teacher);
        for (Student student : students)
            session.saveOrUpdate(student);

        session.flush();
    }

    public synchronized static List<ScheduledTest> getScheduledTests() throws Exception {

        List<ScheduledTest> scheduledTests;
        try (Session session = sessionFactory.openSession()) {
            session.beginTransaction();

            String hql = "SELECT st FROM ScheduledTest st JOIN FETCH st.examForm et";
            Query query = session.createQuery(hql, ScheduledTest.class);
            scheduledTests = query.getResultList();

            for (ScheduledTest scheduledTest : scheduledTests) {
                int timeLimit = scheduledTest.getExamForm().getTimeLimit();
            }
            session.getTransaction().commit();
            return scheduledTests;
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }

    public static synchronized List<ScheduledTest> getScheduledTestsActive() throws Exception {
        List<ScheduledTest> scheduledTests;
        Session session = sessionFactory.openSession();
        session.beginTransaction();

        String hql = "SELECT st FROM ScheduledTest st JOIN FETCH st.examForm et WHERE st.status IN(1,0)";
        Query query = session.createQuery(hql, ScheduledTest.class);
        scheduledTests = query.getResultList();

        session.getTransaction().commit();
        session.close();
        return scheduledTests;
    }

    public static List<Student> getAllStudents() throws Exception {

        List<Student> students = new ArrayList<Student>();
        Session session = sessionFactory.openSession();
        session.beginTransaction();

        //
        String queryString = "SELECT s FROM Student s";
        Query query = session.createQuery(queryString, Student.class);
        students = query.getResultList();
        //
        session.getTransaction().commit();
        session.close();
        return students;
    }


    public static Teacher getTeacher() {
        // create a Criteria object for the Teacher class
        Criteria criteria = session.createCriteria(Teacher.class);
// set the first result to 0 (i.e., the first row)
        criteria.setFirstResult(0);
// set the maximum number of results to 1 (i.e., only one row)
        criteria.setMaxResults(1);
// execute the query and get the result
        Teacher firstTeacher = (Teacher) criteria.uniqueResult();

        return firstTeacher;
    }

    public static List<String> getListExamFormCode(String teacherId) {
        Session session = sessionFactory.openSession();
        session.beginTransaction();
        Teacher teacher = session.get(Teacher.class, teacherId);
        org.hibernate.Query<String> query = session.createQuery("SELECT code FROM ExamForm WHERE subject IN (:subjects)", String.class);
        query.setParameterList("subjects", teacher.getSubjects());
        List<String> codes = query.getResultList();
        session.getTransaction().commit();
        session.close();
        return codes;
    }

    public static ExamForm getExamForm(String examFormId) {
        Session session = sessionFactory.openSession();
        session.beginTransaction();
        Query query = session.createQuery("FROM ExamForm ef WHERE  ef.code = :examFormId", ExamForm.class);
        query.setParameter("examFormId", examFormId);
        ExamForm examForm = (ExamForm) query.getSingleResult();
        session.getTransaction().commit();
        session.close();
        return examForm;
    }

    public static boolean addScheduleTest(ScheduledTest scheduledTest) {
        try {
            Session session = sessionFactory.openSession();
            session.beginTransaction();
            session.saveOrUpdate(scheduledTest);
            session.flush();
            session.getTransaction().commit();
            session.close();
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    public synchronized static boolean updateScheduleTestStatus(ScheduledTest scheduledTest, int newStatus) {
        try {
            Session session = sessionFactory.openSession();
            session.beginTransaction();

            String queryString = "update ScheduledTest st " +
                    "set st.status=:newStatus where st.id =:id";
            Query query = session.createQuery(queryString);
            query.setParameter("id", scheduledTest.getId());
            query.setParameter("newStatus", newStatus);
            int rowsAffected = query.executeUpdate();
            System.out.println(rowsAffected + " affected ");

            session.getTransaction().commit();
            session.close();
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    public synchronized static void saveExtraTimeRequest(ExtraTime extraTime) {
        Session session = sessionFactory.openSession();
        session.beginTransaction();
        session.saveOrUpdate(extraTime);
        session.flush();
        session.getTransaction().commit();
        session.close();
    }


    public static List<ExtraTime> getAllExtraTimes() {
        List<ExtraTime> extraTimes = new ArrayList<>();
        Session session = sessionFactory.openSession();
        session.beginTransaction();

        // Retrieve all ExtraTime records
        String selectQuery = "SELECT e FROM ExtraTime e";
        extraTimes = session.createQuery(selectQuery, ExtraTime.class).getResultList();

        // Delete all data from ExtraTime table
        String deleteQuery = "DELETE FROM ExtraTime";
        Query deleteQueryObj = session.createQuery(deleteQuery);
        deleteQueryObj.executeUpdate();

        session.getTransaction().commit();
        session.close();

        return extraTimes;
    }


    public static void updateScheduleTests(List<ScheduledTest> scheduledTests, SessionFactory sessionFactory) throws Exception {
        Session session = sessionFactory.openSession();
        session.beginTransaction();
        for (ScheduledTest scheduledTest : scheduledTests) {
            System.out.println("update " + scheduledTest.getId() + " status " + scheduledTest.getStatus());
            session.saveOrUpdate(scheduledTest);
        }
        session.flush();
        session.getTransaction().commit();
        session.close();
    }

    public static List<Subject> getSubjectsFromTeacherId(String id) {
        List<Subject> subjects = new ArrayList<>();
        Session session = sessionFactory.openSession();
        Teacher teacher = session.get(Teacher.class, id);
        String queryString = "SELECT s FROM Subject s WHERE :teacher IN elements(s.teachers)";
        Query query = session.createQuery(queryString, Subject.class);
        query.setParameter("teacher", teacher);
        subjects = query.getResultList();
        session.close();
        return subjects;
    }

    public static List<Subject> getAllSubjects() {
        List<Subject> subjects;
        Session session = sessionFactory.openSession();
        String queryString = "SELECT s FROM Subject s";
        Query query = session.createQuery(queryString, Subject.class);
        subjects = query.getResultList();
        session.close();
        return subjects;

    }

    public static Teacher getTeacherFromId(String id) {
        Session session = sessionFactory.openSession();
        Teacher teacher = session.get(Teacher.class, id);
        session.close();
        return teacher;
    }

    public static Principal getPrincipalFromId(String id) {
        Session session = sessionFactory.openSession();
        Principal principal = session.get(Principal.class, id);
        session.close();
        return principal;
    }

    public static List<Course> getCoursesFromSubjectName(String subjectName) {
        List<Course> courses = new ArrayList<>();
        Session session = sessionFactory.openSession();
        String querySub = "SELECT s FROM Subject s WHERE s.name =:subjectName";
        Query q = session.createQuery(querySub, Subject.class);
        q.setParameter("subjectName", subjectName);
        Subject subject = (Subject) q.getSingleResult();
        String queryString = "SELECT c FROM Course c JOIN c.subject s WHERE s = :subject ";
        courses = session.createQuery(queryString, Course.class)
                .setParameter("subject", subject)
                .getResultList();
        for (Course course : courses)
            course.setSubject(subject);
        session.close();
        return courses;
    }

    public static Course getCourseFromCourseName(String courseName) {
         session = null;
        Course course = null;

        try {
            Session session = sessionFactory.openSession();
            String querySub = "SELECT c FROM Course c WHERE c.name = :courseName";
            org.hibernate.Query<Course> query = session.createQuery(querySub, Course.class);
            query.setParameter("courseName", courseName);
            List<Course> results = query.getResultList();
            if (!results.isEmpty()) {
                course = results.get(0);
            }
        } catch (NoResultException nre) {
            // No course found, return null or handle the situation as needed
            course = null;
        } finally {
            if (session != null) {
                session.close();
            }
        }

        return course;
    }


    public static List<Question> getQuestionsFromCourseName(String courseName) {
        List<Question> questions = new ArrayList<>();
        session = null;

        try {
            Session session = sessionFactory.openSession();
            String querySub = "SELECT c FROM Course c WHERE c.name = :courseName";
            org.hibernate.Query<Course> query = session.createQuery(querySub, Course.class);
            query.setParameter("courseName", courseName);
            Course course = (Course) query.getSingleResult();

            String queryString = "SELECT DISTINCT q FROM Course c JOIN c.questions q WHERE c = :course";
            org.hibernate.Query<Question> questionQuery = session.createQuery(queryString, Question.class);
            questionQuery.setParameter("course", course);
            questions = questionQuery.getResultList();
        } catch (NoResultException nre) {
            // No course found, return empty list or handle the situation as needed
            questions = new ArrayList<>();
        } finally {
            if (session != null) {
                session.close();
            }
        }

        return questions;
    }

    public static Question getQuestionToUpdate(Question question){
        Session session = sessionFactory.openSession();
        session.beginTransaction();

        String  queryString ="select q from Question q join fetch q.courses " +
                "where q.id =:id";
        Query query = session.createQuery(queryString,Question.class);
        query.setParameter("id",question.getId());
        Question question1 = (Question) query.getSingleResult();
        session.getTransaction().commit();
        session.close();
        for(Course cour : question1.getCourses())
            System.out.println(cour.getName());
        return question1;
    }


    public static void addExamForm(ExamForm examForm) {
        Session session = sessionFactory.openSession();
        session.beginTransaction();
        session.save(examForm);
        session.flush();
        session.getTransaction().commit();
        session.close();
    }

    public static void addQuestionScores(List<Question_Score> questionScores) {
        Session session = sessionFactory.openSession();
        session.beginTransaction();
        for(Question_Score questionScore:questionScores){
            session.save(questionScore);
        }
        session.flush();
        session.getTransaction().commit();
        session.close();
    }

    public static List<StudentTest> getStudentTests(Student student){
        Session session = sessionFactory.openSession();
        session.beginTransaction();
        Query query = session.createQuery("FROM StudentTest s WHERE s.student = :studentToTake", StudentTest.class);
        query.setParameter("studentToTake", student);
        List<StudentTest> studentTests = query.getResultList();
        student.setStudentTests(studentTests);
        for (StudentTest studentTest : studentTests) {
            Query query2 = session.createQuery("FROM ScheduledTest s WHERE :studentTest IN elements(s.studentTests)", ScheduledTest.class);
            query2.setParameter("studentTest", studentTest);
            ScheduledTest scheduledTest = (ScheduledTest) query2.getSingleResult();

            studentTest.setScheduledTest(scheduledTest);
            ExamForm examForm = scheduledTest.getExamForm();
            studentTest.setSubject(scheduledTest,examForm.getSubject());
            studentTest.setCourse(scheduledTest,examForm.getCourse());
            studentTest.setStudent(student);
            Teacher teacher = scheduledTest.getTeacher();
            studentTest.setTeacher(scheduledTest,teacher);
        }

        session.getTransaction().commit();
        session.close();

        return studentTests;
    }

    public static List<StudentTest> getStudentTestsFromScheduled(ScheduledTest scheduledTest){
        Session session = sessionFactory.openSession();
        session.beginTransaction();
        Query query = session.createQuery("FROM StudentTest s WHERE s.scheduledTest = :scheduledTest", StudentTest.class);
        query.setParameter("scheduledTest", scheduledTest);
        List<StudentTest> studentTests = query.getResultList();
        session.getTransaction().commit();
        session.close();

        return studentTests;
    }

    public static StudentTest getStudentTest(StudentTest studentTest){
        Session session = sessionFactory.openSession();
        session.beginTransaction();
        StudentTest studentTestToReturn = session.get(StudentTest.class,studentTest.getId());
        Query query = session.createQuery("select qa from Question_Answer qa join fetch qa.questionScore qs " +
                                          "join fetch qs.question " +
                                          "where qa.studentTest =:studentTest");
        query.setParameter("studentTest",studentTestToReturn);
        List<Question_Answer> questionAnswers = query.getResultList();

        studentTestToReturn.setQuestionAnswers(questionAnswers);
        session.getTransaction().commit();
        session.close();
        return studentTestToReturn;
    }

    public static void updateStudentTest(StudentTest stud){
        Session session = sessionFactory.openSession();
        session.beginTransaction();
        session.saveOrUpdate(stud);
        session.flush();
        session.getTransaction().commit();
        session.close();
    }

    public static String login_auth(String username, String password){
        Session session = sessionFactory.openSession();
        session.beginTransaction();

// Check in the student table
        String studentQuery = "SELECT 'student' as type, loggedIn FROM Student WHERE id = :username AND password = :password";
        List<Object[]> studentResults = session.createNativeQuery(studentQuery)
                .setParameter("username", username)
                .setParameter("password", password)
                .getResultList();

// Check in the manager table
        String managerQuery = "SELECT 'manager' as type, loggedIn FROM principal WHERE id = :username AND password = :password";
        List<Object[]> managerResults = session.createNativeQuery(managerQuery)
                .setParameter("username", username)
                .setParameter("password", password)
                .getResultList();

// Check in the teacher table
        String teacherQuery = "SELECT 'teacher' as type, loggedIn FROM Teacher WHERE id = :username AND password = :password";
        List<Object[]> teacherResults = session.createNativeQuery(teacherQuery)
                .setParameter("username", username)
                .setParameter("password", password)
                .getResultList();


// Combine the results and determine the user type
        String userType = null;
        Boolean loggedIn = null;

        if (!studentResults.isEmpty()) {
            Object[] studentResult = studentResults.get(0);
            userType = (String) studentResult[0];
            loggedIn = (boolean) studentResult[1];
            if(loggedIn){userType = "logged_error";}
            else{
                String updateLoggedInQuery = "UPDATE student SET loggedIn = true WHERE id = :username";
                session.createNativeQuery(updateLoggedInQuery)
                        .setParameter("username", username)
                        .executeUpdate();
            }
        } else if (!managerResults.isEmpty()) {
            Object[] managerResult = managerResults.get(0);
            userType = (String) managerResult[0];
            loggedIn = (boolean) managerResult[1];
            if(loggedIn){userType = "logged_error";}
            else{
                String updateLoggedInQuery = "UPDATE principal SET loggedIn = true WHERE id = :username";
                session.createNativeQuery(updateLoggedInQuery)
                        .setParameter("username", username)
                        .executeUpdate();
            }
        } else if (!teacherResults.isEmpty()) {
            Object[] teacherResult = teacherResults.get(0);
            userType = (String) teacherResult[0];
            loggedIn = (boolean) teacherResult[1];
            if(loggedIn){userType = "logged_error";}
            else{
                String updateLoggedInQuery = "UPDATE Teacher SET loggedIn = true WHERE id = :username";
                session.createNativeQuery(updateLoggedInQuery)
                        .setParameter("username", username)
                        .executeUpdate();
            }
        }


        if (userType != null) {
            // User exists, userType contains the user type
            System.out.format("%s %s connecting to system", userType, username);
        }
        if(userType == null){userType = "wrong";}
        session.getTransaction().commit();
        session.close();
        return userType;
    }

    public static void logout(String username, String type) {
        Session session = sessionFactory.openSession();
        session.beginTransaction();
        String updateLoggedInQuery = null;
        switch (type){
            case ("student"):
                updateLoggedInQuery = "UPDATE student SET loggedIn = false WHERE id = :username";
                break;
            case ("teacher"):
                updateLoggedInQuery = "UPDATE Teacher SET loggedIn = false WHERE id = :username";
                break;
            case ("manager"):
                updateLoggedInQuery = "UPDATE principal SET loggedIn = false WHERE id = :username";
                break;
        }
        session.createNativeQuery(updateLoggedInQuery)
                .setParameter("username", username)
                .executeUpdate();
        session.getTransaction().commit();
        session.close();
        System.out.println(type + " " + username + " logged out successfully");
    }

    public static boolean addQuestion(Question question){
        try {
        Session session = sessionFactory.openSession();
        session.beginTransaction();
        session.save(question);

        session.flush();
        session.getTransaction().commit();
        session.close();
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    public static boolean updateScheduleTest(ScheduledTest scheduledTest) {
        try {
            Session session = sessionFactory.openSession();
            session.beginTransaction();
            session.saveOrUpdate(scheduledTest);
            session.flush();
            session.getTransaction().commit();
            session.close();
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    public static boolean updateSubmissions_Active_Start(String id){
        try {
            Session session = sessionFactory.openSession();
            session.beginTransaction();

            String queryString ="update ScheduledTest st " +
                    "set st.activeStudents= st.activeStudents+1 " +
                    "where st.id =:id";
            Query query = session.createQuery(queryString);
            query.setParameter("id",id);
            int rowsAffected = query.executeUpdate();
            System.out.println(rowsAffected + " affected ");

            session.getTransaction().commit();
            session.close();

        }catch (Exception e){
            e.printStackTrace();
            return false;
        }
        return true;
    }

    public static boolean updateSubmissions_Active_Finish(String id){
        try {
            Session session = sessionFactory.openSession();
            session.beginTransaction();

            String queryString ="update ScheduledTest st " +
                    "set st.activeStudents= st.activeStudents-1 , st.submissions = st.submissions+1" +
                    " where st.id =:id";
            Query query = session.createQuery(queryString);
            query.setParameter("id",id);
            int rowsAffected = query.executeUpdate();
            System.out.println(rowsAffected + " affected ");

            session.getTransaction().commit();
            session.close();

        }catch (Exception e){
            e.printStackTrace();
            return false;
        }
        return true;
    }

    public static List<ExamForm> getCourseExamForms(String courseName) {
        List<ExamForm> examForms = new ArrayList<>();
        Session session = null;

        try {
            session = sessionFactory.openSession();
            String querySub = "SELECT c FROM Course c WHERE c.name = :courseName";
            org.hibernate.Query<Course> query = session.createQuery(querySub, Course.class);
            query.setParameter("courseName", courseName);
            Course course = (Course) query.getSingleResult();

            String queryString = "SELECT DISTINCT e FROM Course c JOIN c.examForms e join fetch e.teacher WHERE c = :course";
            org.hibernate.Query<ExamForm> examFormQuery = session.createQuery(queryString, ExamForm.class);
            examFormQuery.setParameter("course", course);
            examForms = examFormQuery.getResultList();
        } catch (NoResultException nre) {
            // No course found, return empty list or handle the situation as needed
            examForms = new ArrayList<>();
        } finally {
            if (session != null) {
                session.close();
            }
        }

        return examForms;
    }



    public static List<Question_Score> getQuestionScoresFromExamForm(ExamForm examForm) {
        Session session = sessionFactory.openSession();
        String queryString = "SELECT qs FROM Question_Score qs WHERE qs.examForm =:examForm";
        Query query = session.createQuery(queryString, Question_Score.class);
        query.setParameter("examForm",examForm);
        List<Question_Score> questionScores = query.getResultList();
        session.close();
        return questionScores;
    }

    public synchronized static ScheduledTest getScheduleTest(String id){
        ScheduledTest scheduledTest;
        Session session = sessionFactory.openSession();
        String queryString = "SELECT st from ScheduledTest st where id =:id";
        Query query = session.createQuery(queryString,ScheduledTest.class);
        query.setParameter("id",id);
        scheduledTest = (ScheduledTest) query.getSingleResult();
        session.close();
        return scheduledTest;
    }
    public synchronized static ScheduledTest getScheduleTestWithInfo(String id){
        ScheduledTest scheduledTest;
        Session session = sessionFactory.openSession();
        scheduledTest = session.get(ScheduledTest.class,id);
        String qString = "SELECT e FROM ExamForm e WHERE :scheduleTest in elements(e.scheduledTests) ";
        Query query = session.createQuery(qString, ExamForm.class);
        query.setParameter("scheduleTest",scheduledTest);
        ExamForm examForm = (ExamForm) query.getSingleResult();

        String hql = "SELECT qs FROM Question_Score qs " +
                "JOIN FETCH qs.question " +
                "WHERE qs.examForm = :examForm";

        List<Question_Score> questionScores = session.createQuery(hql)
                .setParameter("examForm", examForm)
                .getResultList();

        examForm.setQuestionScores(questionScores);
        scheduledTest.setExamForm(examForm);
        session.close();
        return scheduledTest;
    }

    public static Student getStudent(String id){
        Session session = sessionFactory.openSession();
        Student student = session.get(Student.class,id);
        String queryString = "SELECT st from StudentTest st where st.student =:student";
        Query query = session.createQuery(queryString, StudentTest.class);
        query.setParameter("student",student);
        List<StudentTest> studentTests = query.getResultList();
        student.setStudentTests(studentTests);
        session.close();
        System.out.println(student.getEmail());
        return student;
    }

    public static boolean saveQuestionAnswers(List<Object> items){
        try{
            Student student = (Student) items.get(0);
            StudentTest studentTest = (StudentTest) items.get(1);

            Session session = sessionFactory.openSession();
            session.beginTransaction();
            session.saveOrUpdate(studentTest);
            for(int i=2;i<items.size();i++){
                Question_Answer item = (Question_Answer) items.get(i);
                System.out.println("saving question answer "+ item.getId());
                System.out.println("in question answer q.s id "+ item.getQuestionScore().getId());
                System.out.println("in question answer st id "+ item.getStudentTest().getId());
                session.save(item);
            }
            session.flush();
            session.getTransaction().commit(); // Save Everything in the transaction area
            session.close();
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    public static void saveQuestionScores(List<Question_Score> items){
        Session session = sessionFactory.openSession();
        session.beginTransaction();
        for(Question_Score item:items){
            session.saveOrUpdate(item);
        }
        session.flush();
        session.getTransaction().commit(); // Save Everything in the transaction area
        session.close();

    }

    public static void saveStudentTest(List<Object> student_studentTest){
        Student student = (Student) student_studentTest.get(0);
        StudentTest studentTest = (StudentTest) student_studentTest.get(1);

        Session session = sessionFactory.openSession();
        session.beginTransaction();
        session.saveOrUpdate(student);
        session.saveOrUpdate(studentTest);
        session.flush();
        session.getTransaction().commit(); // Save Everything in the transaction area
        session.close();

    }

    public static List<Statistics> getTeacherExamStats(String teacherId) {
        Session session = sessionFactory.openSession(); // Open a new session

        Query query = session.createQuery(
                "SELECT e.scheduledTest.id as st,e.scheduledTest.date, AVG(e.grade) AS average " +
                        "FROM StudentTest e " +
                        "WHERE e.scheduledTest.teacher.id = :teacherId and e.scheduledTest.isComputerTest = true " +
                        "GROUP BY st"
        );
        query.setParameter("teacherId", teacherId);
        List<Object[]> results = query.getResultList();

        List<Statistics> examStatsList = new ArrayList<>();

        for (Object[] result : results) {
            String scheduleTestId = (String) result[0];
            LocalDate date = (LocalDate) result[1];
            Double averageGrade = (Double) result[2];

            Query gradeQuery = session.createQuery(
                    "SELECT e.grade " +
                            "FROM StudentTest e " +
                            "WHERE e.scheduledTest.teacher.id = :teacherId and e.scheduledTest.id = :scheduleTestId " +
                            "order by e.grade"
            );
            gradeQuery.setParameter("teacherId", teacherId);
            gradeQuery.setParameter("scheduleTestId", scheduleTestId);
            List<Integer> grades = gradeQuery.getResultList();

            int median;
            int size = grades.size();
            if (size % 2 == 0) {
                median = grades.get(size / 2 - 1);
            } else {
                median = grades.get(size / 2);
            }

            List<Double> distribution = new ArrayList<>();
            for (int i = 0; i < 11; i++) {
                distribution.add(0.0);
            }

            int totalCount = grades.size();
            for (int grade : grades) {
                double s;
                if (grade == 100)
                    s = distribution.get(9);
                else
                    s = distribution.get(grade / 10);
                if (grade == 100)
                    distribution.set(9, s + 1);
                distribution.set(grade / 10, s + 1);
            }
            for (int i = 0; i < 11; i++) {
                distribution.set(i, (distribution.get(i) / totalCount) * 100);
            }

            Statistics statistics = new Statistics();
            statistics.setScheduleTestId(scheduleTestId);
            statistics.setAvgGrade(averageGrade);
            statistics.setMedian(median);
            statistics.setDistribution(distribution);
            statistics.setDate(date.of(date.getYear(), date.getMonth(),date.getDayOfMonth()));

            examStatsList.add(statistics);
        }

        session.close();
        return examStatsList;
    }



    public static List<Statistics> getCourseExamStats(int courseId) {
        SessionFactory sessionFactory = getSessionFactory();
        Session session = sessionFactory.openSession(); // Open a new session

        Query query = session.createQuery(
                "SELECT e.scheduledTest.id as st,e.scheduledTest.date, AVG(e.grade) AS average " +
                        "FROM StudentTest e " +
                        "WHERE e.scheduledTest.examForm.course.id = :courseId and e.scheduledTest.isComputerTest = true " +
                        "GROUP BY st"
        );
        query.setParameter("courseId", courseId);
        List<Object[]> results = query.getResultList();

        List<Statistics> examStatsList = new ArrayList<>();

        for (Object[] result : results) {
            String scheduleTestId = (String) result[0];
            LocalDate date = (LocalDate) result[1];
            Double averageGrade = (Double) result[2];

            Query gradeQuery = session.createQuery(
                    "SELECT e.grade " +
                            "FROM StudentTest e " +
                            "WHERE e.scheduledTest.examForm.course.id = :courseId and e.scheduledTest.id = :scheduleTestId " +
                            "order by e.grade"
            );
            gradeQuery.setParameter("courseId", courseId);
            gradeQuery.setParameter("scheduleTestId", scheduleTestId);
            List<Integer> grades = gradeQuery.getResultList();

            int median;
            int size = grades.size();
            if (size % 2 == 0) {
                median = grades.get(size / 2 - 1);
            } else {
                median = grades.get(size / 2);
            }

            List<Double> distribution = new ArrayList<>();
            for (int i = 0; i < 11; i++) {
                distribution.add(0.0);
            }

            int totalCount = grades.size();
            for (int grade : grades) {
                double s;
                if (grade == 100)
                    s = distribution.get(9);
                else
                    s = distribution.get(grade / 10);
                if (grade == 100)
                    distribution.set(9, s + 1);
                distribution.set(grade / 10, s + 1);
            }
            for (int i = 0; i < 11; i++) {
                distribution.set(i, (distribution.get(i) / totalCount) * 100);
            }

            Statistics statistics = new Statistics();
            statistics.setScheduleTestId(scheduleTestId);
            statistics.setAvgGrade(averageGrade);
            statistics.setMedian(median);
            statistics.setDistribution(distribution);
            statistics.setDate(date.of(date.getYear(), date.getMonth(),date.getDayOfMonth()));

            examStatsList.add(statistics);
        }

        session.close();
        return examStatsList;
    }


    public static Statistics getStudentExamStats(String studentId) {
        Session session = sessionFactory.openSession(); // Open a new session
        List<Integer> grades = new ArrayList<>();

        Query query = session.createQuery(
                "SELECT e.grade " +
                        "FROM StudentTest e " +
                        "WHERE e.student.id = :studentId and e.scheduledTest.isComputerTest = true " +
                        "order by e.grade"
        );
        query.setParameter("studentId", studentId);
        grades = query.getResultList();
        if(grades.size() == 0){return null;}
        double sum = 0;
        for (Integer grade : grades) {
            sum += grade;
        }
        double average = sum / grades.size();

        int med = grades.size() / 2;
        int median = 0;
        if (grades.size() % 2 == 0) {
            if(med == 0){median = grades.get(med);};
            median = grades.get(med - 1);
        } else {
            median = grades.get(med);
        }

        List<Double> distribution = new ArrayList<>();
        for (int i = 0; i < 11; i++) {
            distribution.add(0.0);
        }

        int totalCount = grades.size();
        for (Integer grade : grades) {
            double s;
            if (grade == 100)
                s = distribution.get(9);
            else
                s = distribution.get(grade / 10);
            if (grade == 100)
                distribution.set(9, s + 1);
            distribution.set(grade / 10, s + 1);
        }
        for (int i = 0; i < 11; i++) {
            distribution.set(i, (distribution.get(i) / totalCount) * 100);
        }

        Statistics statistics = new Statistics();
        statistics.setScheduleTestId(null); // Set to null as it's not applicable for individual student statistics
        statistics.setAvgGrade(average);
        statistics.setMedian(median);
        statistics.setStudentId(studentId);
        statistics.setDistribution(distribution);


        session.close();
        return statistics;
    }

    public static List<Statistics> getTeacherWriterExamStats(String teacherId) {
        SessionFactory sessionFactory = getSessionFactory();
        Session session = sessionFactory.openSession(); // Open a new session

        Query query = session.createQuery(
                "SELECT e.scheduledTest.id as st,e.scheduledTest.date, AVG(e.grade) AS average " +
                        "FROM StudentTest e " +
                        "WHERE e.scheduledTest.examForm.teacher.id = :teacherId and e.scheduledTest.isComputerTest = true " +
                        "GROUP BY st"
        );
        query.setParameter("teacherId", teacherId);
        List<Object[]> results = query.getResultList();

        List<Statistics> examStatsList = new ArrayList<>();

        for (Object[] result : results) {
            String scheduleTestId = (String) result[0];
            LocalDate date = (LocalDate) result[1];
            Double averageGrade = (Double) result[2];

            Query gradeQuery = session.createQuery(
                    "SELECT e.grade " +
                            "FROM StudentTest e " +
                            "WHERE e.scheduledTest.examForm.teacher.id = :teacherId and e.scheduledTest.id = :scheduleTestId " +
                            "order by e.grade"
            );
            gradeQuery.setParameter("teacherId", teacherId);
            gradeQuery.setParameter("scheduleTestId", scheduleTestId);
            List<Integer> grades = gradeQuery.getResultList();

            int median;
            int size = grades.size();
            if (size % 2 == 0) {
                median = grades.get(size / 2 - 1);
            } else {
                median = grades.get(size / 2);
            }

            List<Double> distribution = new ArrayList<>();
            for (int i = 0; i < 11; i++) {
                distribution.add(0.0);
            }

            int totalCount = grades.size();
            for (int grade : grades) {
                double s;
                if (grade == 100)
                    s = distribution.get(9);
                else
                    s = distribution.get(grade / 10);
                if (grade == 100)
                    distribution.set(9, s + 1);
                distribution.set(grade / 10, s + 1);
            }
            for (int i = 0; i < 11; i++) {
                distribution.set(i, (distribution.get(i) / totalCount) * 100);
            }

            Statistics statistics = new Statistics();
            statistics.setScheduleTestId(scheduleTestId);
            statistics.setAvgGrade(averageGrade);
            statistics.setMedian(median);
            statistics.setDistribution(distribution);
            statistics.setDate(date.of(date.getYear(), date.getMonth(),date.getDayOfMonth()));


            examStatsList.add(statistics);
        }

        session.close();
        return examStatsList;
    }

    public static void saveFileToDatabase(TestFile testFile) {
        Session session = sessionFactory.openSession();
        session.beginTransaction();
        System.out.println("trying to save local test " + testFile.getFileName());
        session.saveOrUpdate(testFile);
        session.flush();
        System.out.println("save local test " + testFile.getFileName());
        session.getTransaction().commit();
        session.close();
    }

    public static void deleteScheduleTest(ScheduledTest deleteScheduledTest) {
        Session session=sessionFactory.openSession();
        session.beginTransaction();
        session.delete(deleteScheduledTest);
        session.flush();
        session.getTransaction().commit();
        session.close();
    }
    public static String generateUniqueQuestionCode(String subjectCode) {
        String uniqueCode = null;
        boolean isUnique = false;

        try  {
            Session session = sessionFactory.openSession();
            Transaction transaction = session.beginTransaction();

            while (!isUnique) {
                int randomNumber = (int) (Math.random() * 1000);
                String formattedCode = subjectCode + String.format("%03d", randomNumber);

                Query  query = session.createQuery("SELECT COUNT(*) FROM Question WHERE id = :formattedCode", Long.class);
                query.setParameter("formattedCode", formattedCode);
                int count = Integer.parseInt(query.getSingleResult().toString());

                if (count == 0) {
                    uniqueCode = formattedCode;
                    isUnique = true;
                }
            }

            transaction.commit();
            session.close();
        } catch (Exception e) {
            // Handle exception
        }
        return uniqueCode;
    }

public static String generateUniqueExamCode(String examCode) {
    String uniqueCode = null;
    boolean isUnique = false;

    try {
        Session session = sessionFactory.openSession() ;
        Transaction transaction = session.beginTransaction();

        while (!isUnique) {
            int randomNumber = (int) (Math.random() * 100);
            String formattedCode = examCode + String.format("%02d", randomNumber);

            Query  query = session.createQuery("SELECT COUNT(*) FROM ExamForm WHERE code = :formattedCode", Long.class);
            query.setParameter("formattedCode", formattedCode);
            int count = Integer.parseInt(query.getSingleResult().toString());

            if (count == 0) {
                uniqueCode = formattedCode;
                isUnique = true;
            }
        }

        transaction.commit();
        session.close();
    } catch (Exception e) {
        // Handle exception
    }
    return uniqueCode;
}



    public static boolean getFirstTestEntryCheck(String studentId, String scheduleTestId) {
        boolean isFirstTime=true;
        Session session = sessionFactory.openSession();
        session.beginTransaction();

        String queryString ="SELECT st from StudentTest st " +
                "WHERE st.student.id=:studentId and st.scheduledTest.id =:scheduleTestId";

        Query query = session.createQuery(queryString,StudentTest.class);
        query.setParameter("scheduleTestId",scheduleTestId);
        query.setParameter("studentId",studentId);
        List<StudentTest> resultList = query.getResultList();
        if (!resultList.isEmpty()){
            System.out.println("not empty result");
            isFirstTime = false;
        }
        session.getTransaction().commit();
        session.close();
        return isFirstTime;
    }

    public static void logOffAllUsers(){
        Session session = sessionFactory.openSession();
        session.beginTransaction();

        String hqlQuery = "UPDATE Student SET loggedIn = false WHERE loggedIn = true ";
        Query query = session.createQuery(hqlQuery);
        int rowCount = query.executeUpdate();

         hqlQuery = "UPDATE Teacher SET loggedIn = false WHERE loggedIn = true ";
         query = session.createQuery(hqlQuery);
         rowCount = query.executeUpdate();

         hqlQuery = "UPDATE Principal SET loggedIn = false WHERE loggedIn = true ";
         query = session.createQuery(hqlQuery);
         rowCount = query.executeUpdate();

        session.getTransaction().commit();
        session.close();

    }


    public static List<Teacher> getAllTeacherNames() throws Exception
    {
        List<Teacher> teachers = new ArrayList<Teacher>();
        SessionFactory sessionFactory = getSessionFactory();
        session = sessionFactory.openSession();
        //
        String queryString = "FROM Teacher";
        Query query = session.createQuery(queryString,Teacher.class);
        teachers = query.getResultList();
        //
        session.close();
        return teachers;

    }

    public static List<Course> getAllCourseNames() throws Exception
    {
        List<Course> courses = new ArrayList<Course>();
        SessionFactory sessionFactory = getSessionFactory();
        session = sessionFactory.openSession();
        //
        String queryString = "FROM Course s";
        Query query = session.createQuery(queryString);
        courses = query.getResultList();
        //
        session.close();
        return courses;

    }

    public static List<String> getAllStudentNames() throws Exception{

        List<String> students = new ArrayList<String>();
        SessionFactory sessionFactory = getSessionFactory();
        session = sessionFactory.openSession();
        //
        String queryString = "FROM Student s";
        Query query = session.createQuery(queryString);
        students = query.getResultList();
        //
        session.close();
        return students;
    }






}
