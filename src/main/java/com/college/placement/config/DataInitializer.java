package com.college.placement.config;

import com.college.placement.entity.*;
import com.college.placement.enums.*;
import com.college.placement.repository.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileOutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Component
public class DataInitializer implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(DataInitializer.class);

    private final UserRepository userRepository;
    private final StudentProfileRepository studentProfileRepository;
    private final CompanyRepository companyRepository;
    private final PlacementDriveRepository placementDriveRepository;
    private final ApplicationRepository applicationRepository;
    private final ApplicationStatusHistoryRepository statusHistoryRepository;
    private final InterviewRepository interviewRepository;
    private final PlacementOfferRepository offerRepository;
    private final NotificationRepository notificationRepository;
    private final AuditLogRepository auditLogRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${app.upload.dir:uploads/resumes}")
    private String uploadDir;

    public DataInitializer(UserRepository userRepository,
                           StudentProfileRepository studentProfileRepository,
                           CompanyRepository companyRepository,
                           PlacementDriveRepository placementDriveRepository,
                           ApplicationRepository applicationRepository,
                           ApplicationStatusHistoryRepository statusHistoryRepository,
                           InterviewRepository interviewRepository,
                           PlacementOfferRepository offerRepository,
                           NotificationRepository notificationRepository,
                           AuditLogRepository auditLogRepository,
                           PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.studentProfileRepository = studentProfileRepository;
        this.companyRepository = companyRepository;
        this.placementDriveRepository = placementDriveRepository;
        this.applicationRepository = applicationRepository;
        this.statusHistoryRepository = statusHistoryRepository;
        this.interviewRepository = interviewRepository;
        this.offerRepository = offerRepository;
        this.notificationRepository = notificationRepository;
        this.auditLogRepository = auditLogRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {
        initSampleResumeFolder();

        if (userRepository.count() > 0) {
            log.info("Database already initialized with records.");
            return;
        }

        log.info("Seeding initial demonstration data...");

        // 1. Create Admin
        User admin = new User("admin@college.edu", passwordEncoder.encode("Admin@123"), "Dr. Rajesh Kulkarni (Dean Placements)", RoleType.ROLE_ADMIN);
        userRepository.save(admin);

        // 2. Create Placement Officer
        User officer = new User("officer@college.edu", passwordEncoder.encode("Officer@123"), "Prof. Sunita Sharma (Placement Officer)", RoleType.ROLE_OFFICER);
        userRepository.save(officer);

        // 3. Create Sample Students
        // Student 1: High achiever
        User studentUser1 = new User("rahul.sharma@college.edu", passwordEncoder.encode("Student@123"), "Rahul Sharma", RoleType.ROLE_STUDENT);
        userRepository.save(studentUser1);
        StudentProfile profile1 = new StudentProfile();
        profile1.setUser(studentUser1);
        profile1.setUsn("1CS21CS045");
        profile1.setPhone("+91 9876543210");
        profile1.setGender("Male");
        profile1.setBranch("CSE");
        profile1.setGraduationYear(2025);
        profile1.setCgpa(8.85);
        profile1.setTenthPercentage(92.5);
        profile1.setTwelfthPercentage(89.0);
        profile1.setActiveBacklogs(0);
        profile1.setSkills("Java, Spring Boot, React, MySQL, Docker, REST APIs, Data Structures");
        profile1.setAddress("Bengaluru, Karnataka, India");
        profile1.setLinkedinUrl("https://linkedin.com/in/rahulsharma");
        profile1.setGithubUrl("https://github.com/rahulsharma");
        profile1.setResumeFileName("Rahul_Sharma_Resume.pdf");
        profile1.setResumePath("sample_resume.pdf");
        studentProfileRepository.save(profile1);

        // Student 2: Average achiever
        User studentUser2 = new User("priya.patel@college.edu", passwordEncoder.encode("Student@123"), "Priya Patel", RoleType.ROLE_STUDENT);
        userRepository.save(studentUser2);
        StudentProfile profile2 = new StudentProfile();
        profile2.setUser(studentUser2);
        profile2.setUsn("1IS21IS022");
        profile2.setPhone("+91 9876543211");
        profile2.setGender("Female");
        profile2.setBranch("ISE");
        profile2.setGraduationYear(2025);
        profile2.setCgpa(7.40);
        profile2.setTenthPercentage(85.0);
        profile2.setTwelfthPercentage(82.0);
        profile2.setActiveBacklogs(0);
        profile2.setSkills("Python, Django, JavaScript, SQL, HTML, CSS");
        profile2.setAddress("Mysuru, Karnataka, India");
        profile2.setResumeFileName("Priya_Patel_Resume.pdf");
        profile2.setResumePath("sample_resume.pdf");
        studentProfileRepository.save(profile2);

        // Student 3: Boundary case with backlogs & low CGPA
        User studentUser3 = new User("amit.verma@college.edu", passwordEncoder.encode("Student@123"), "Amit Verma", RoleType.ROLE_STUDENT);
        userRepository.save(studentUser3);
        StudentProfile profile3 = new StudentProfile();
        profile3.setUser(studentUser3);
        profile3.setUsn("1EC21EC010");
        profile3.setPhone("+91 9876543212");
        profile3.setGender("Male");
        profile3.setBranch("ECE");
        profile3.setGraduationYear(2025);
        profile3.setCgpa(6.20);
        profile3.setTenthPercentage(74.0);
        profile3.setTwelfthPercentage(68.5);
        profile3.setActiveBacklogs(2);
        profile3.setSkills("C++, Embedded Systems, Verilog, IoT");
        profile3.setAddress("Hubballi, Karnataka, India");
        studentProfileRepository.save(profile3);

        // Student 4: Top placed student
        User studentUser4 = new User("sneha.rao@college.edu", passwordEncoder.encode("Student@123"), "Sneha Rao", RoleType.ROLE_STUDENT);
        userRepository.save(studentUser4);
        StudentProfile profile4 = new StudentProfile();
        profile4.setUser(studentUser4);
        profile4.setUsn("1CS21CS089");
        profile4.setPhone("+91 9876543213");
        profile4.setGender("Female");
        profile4.setBranch("CSE");
        profile4.setGraduationYear(2025);
        profile4.setCgpa(9.35);
        profile4.setTenthPercentage(96.0);
        profile4.setTwelfthPercentage(94.5);
        profile4.setActiveBacklogs(0);
        profile4.setSkills("Java, Algorithms, System Design, Kubernetes, AWS, Microservices");
        profile4.setPlaced(true);
        profile4.setResumeFileName("Sneha_Rao_Resume.pdf");
        profile4.setResumePath("sample_resume.pdf");
        studentProfileRepository.save(profile4);

        // 4. Create Companies
        Company google = new Company("Google India", "Technology / Software", "Bangalore / Hyderabad", "Anita Sundaram", "google-campus@google.com");
        google.setDescription("Google LLC is an American multinational corporation and technology company focusing on search engine technology, cloud computing, and software development.");
        google.setWebsite("https://careers.google.com");
        google.setHrContactPhone("+91 80 6721 8000");
        google.setLogoUrl("https://upload.wikimedia.org/wikipedia/commons/2/2f/Google_2015_logo.svg");
        companyRepository.save(google);

        Company microsoft = new Company("Microsoft IDC", "Software Development", "Hyderabad / Bangalore", "Kiran Rao", "microsoft-recruit@microsoft.com");
        microsoft.setDescription("Microsoft India Development Center is one of Microsoft's largest R&D centers outside Redmond, building cutting-edge enterprise cloud and AI solutions.");
        microsoft.setWebsite("https://careers.microsoft.com");
        microsoft.setHrContactPhone("+91 80 6699 0000");
        microsoft.setLogoUrl("https://upload.wikimedia.org/wikipedia/commons/9/96/Microsoft_logo_%282012%29.svg");
        companyRepository.save(microsoft);

        Company amazon = new Company("Amazon AWS", "Cloud Computing & E-Commerce", "Bangalore", "Sameer Khan", "aws-college@amazon.com");
        amazon.setDescription("Amazon Web Services (AWS) is the world's most comprehensive and broadly adopted cloud platform, offering over 200 fully featured services.");
        amazon.setWebsite("https://amazon.jobs");
        amazon.setLogoUrl("https://upload.wikimedia.org/wikipedia/commons/a/a9/Amazon_logo.svg");
        companyRepository.save(amazon);

        Company infosys = new Company("Infosys Limited", "IT Services & Consulting", "Mysuru / Bangalore", "Pooja Hegde", "campus@infosys.com");
        infosys.setDescription("Infosys is a global leader in next-generation digital services and consulting, enabling clients across 56 countries to navigate digital transformation.");
        infosys.setWebsite("https://www.infosys.com/careers");
        companyRepository.save(infosys);

        Company gs = new Company("Goldman Sachs", "Investment Banking & FinTech", "Bangalore", "Rohan Mehta", "gs-careers@gs.com");
        gs.setDescription("The Goldman Sachs Group, Inc. is a leading global investment banking, securities, and investment management firm.");
        gs.setWebsite("https://goldmansachs.com/careers");
        companyRepository.save(gs);

        // 5. Create Placement Drives
        LocalDateTime now = LocalDateTime.now();

        // Drive 1: Google SDE
        PlacementDrive drive1 = new PlacementDrive();
        drive1.setTitle("Software Development Engineer (SDE-1)");
        drive1.setCompany(google);
        drive1.setDescription("Seeking high-caliber software engineering graduates with strong algorithmic problem-solving abilities, distributed systems fundamentals, and solid programming foundations in Java/C++.");
        drive1.setJobLocation("Bengaluru, Karnataka");
        drive1.setJobType(JobType.FULL_TIME);
        drive1.setRequiredBranches("CSE,ISE");
        drive1.setMinCgpa(8.0);
        drive1.setMinTenthPercentage(80.0);
        drive1.setMinTwelfthPercentage(80.0);
        drive1.setMaxActiveBacklogs(0);
        drive1.setEligibleGraduationYear(2025);
        drive1.setRequiredSkills("Data Structures, Algorithms, OS, DBMS, Java or C++");
        drive1.setCtcSalary(32.5); // 32.5 LPA
        drive1.setApplicationOpeningDate(now.minusDays(5));
        drive1.setApplicationClosingDate(now.plusDays(10));
        drive1.setVacancies(8);
        drive1.setSelectionProcess("1. Online Coding Assessment (2 coding challenges)\n2. Technical Round 1 (Data Structures)\n3. Technical Round 2 (System Design Fundamentals)\n4. Leadership & Googleyness Fitment Round");
        drive1.setStatus(DriveStatus.PUBLISHED);
        drive1.setCreatedBy(officer);
        placementDriveRepository.save(drive1);

        // Drive 2: Microsoft Cloud Engineer
        PlacementDrive drive2 = new PlacementDrive();
        drive2.setTitle("Software Engineer - Azure Cloud Core");
        drive2.setCompany(microsoft);
        drive2.setDescription("Join Microsoft Azure engineering teams to design and build planet-scale infrastructure, resilient microservices, and enterprise cloud capabilities.");
        drive2.setJobLocation("Hyderabad / Bengaluru");
        drive2.setJobType(JobType.FULL_TIME);
        drive2.setRequiredBranches("CSE,ISE,ECE");
        drive2.setMinCgpa(7.5);
        drive2.setMinTenthPercentage(75.0);
        drive2.setMinTwelfthPercentage(75.0);
        drive2.setMaxActiveBacklogs(0);
        drive2.setEligibleGraduationYear(2025);
        drive2.setRequiredSkills("Object Oriented Design, C# / Java, Networking, Algorithms");
        drive2.setCtcSalary(26.0); // 26 LPA
        drive2.setApplicationOpeningDate(now.minusDays(3));
        drive2.setApplicationClosingDate(now.plusDays(14));
        drive2.setVacancies(12);
        drive2.setSelectionProcess("1. Online Hackathon / Test\n2. Technical Interview 1\n3. Technical Interview 2\n4. AA (As Appropriate) Director Round");
        drive2.setStatus(DriveStatus.PUBLISHED);
        drive2.setCreatedBy(officer);
        placementDriveRepository.save(drive2);

        // Drive 3: Amazon AWS Cloud Support
        PlacementDrive drive3 = new PlacementDrive();
        drive3.setTitle("Cloud Support Associate - AWS Infrastructure");
        drive3.setCompany(amazon);
        drive3.setDescription("Work directly with AWS cloud architects and enterprise clients to troubleshoot, architect, and optimize large-scale distributed cloud systems.");
        drive3.setJobLocation("Bengaluru");
        drive3.setJobType(JobType.FULL_TIME);
        drive3.setRequiredBranches("CSE,ISE,ECE");
        drive3.setMinCgpa(7.0);
        drive3.setMinTenthPercentage(70.0);
        drive3.setMinTwelfthPercentage(70.0);
        drive3.setMaxActiveBacklogs(1);
        drive3.setEligibleGraduationYear(2025);
        drive3.setRequiredSkills("Linux, Networking (TCP/IP), Python/Bash Scripting, AWS Cloud Basics");
        drive3.setCtcSalary(16.5); // 16.5 LPA
        drive3.setApplicationOpeningDate(now.minusDays(2));
        drive3.setApplicationClosingDate(now.plusDays(7));
        drive3.setVacancies(15);
        drive3.setSelectionProcess("1. Online Assessment\n2. Technical Interview (OS & Networks)\n3. Behavioral Interview (Amazon Leadership Principles)");
        drive3.setStatus(DriveStatus.PUBLISHED);
        drive3.setCreatedBy(officer);
        placementDriveRepository.save(drive3);

        // Drive 4: Infosys Specialist Programmer
        PlacementDrive drive4 = new PlacementDrive();
        drive4.setTitle("Specialist Programmer (Digital Development)");
        drive4.setCompany(infosys);
        drive4.setDescription("Fast-track software engineering track focusing on full-stack application development, AI integrations, and digital enterprise transformation.");
        drive4.setJobLocation("Bengaluru / Pune / Hyderabad");
        drive4.setJobType(JobType.FULL_TIME);
        drive4.setRequiredBranches("CSE,ISE,ECE,MECH,CIVIL");
        drive4.setMinCgpa(6.0);
        drive4.setMinTenthPercentage(60.0);
        drive4.setMinTwelfthPercentage(60.0);
        drive4.setMaxActiveBacklogs(2);
        drive4.setEligibleGraduationYear(2025);
        drive4.setRequiredSkills("Java / Python, SQL, Web Technologies");
        drive4.setCtcSalary(9.5); // 9.5 LPA
        drive4.setApplicationOpeningDate(now.minusDays(10));
        drive4.setApplicationClosingDate(now.plusDays(20));
        drive4.setVacancies(40);
        drive4.setSelectionProcess("1. HackWithInfy / Online Coding Test\n2. Technical Interview\n3. HR Verification");
        drive4.setStatus(DriveStatus.PUBLISHED);
        drive4.setCreatedBy(officer);
        placementDriveRepository.save(drive4);

        // 6. Create Applications
        // App 1: Sneha Rao applied to Google and selected
        Application app1 = new Application(profile4, drive1);
        app1.setCurrentStatus(ApplicationStatus.SELECTED);
        app1.setRemarks("Outstanding performance in algorithmic design and problem solving.");
        applicationRepository.save(app1);

        statusHistoryRepository.save(new ApplicationStatusHistory(app1, null, ApplicationStatus.APPLIED, studentUser4, "Initial application"));
        statusHistoryRepository.save(new ApplicationStatusHistory(app1, ApplicationStatus.APPLIED, ApplicationStatus.SHORTLISTED, officer, "Shortlisted based on 9.35 CGPA"));
        statusHistoryRepository.save(new ApplicationStatusHistory(app1, ApplicationStatus.SHORTLISTED, ApplicationStatus.INTERVIEW_SCHEDULED, officer, "Cleared Technical rounds"));
        statusHistoryRepository.save(new ApplicationStatusHistory(app1, ApplicationStatus.INTERVIEW_SCHEDULED, ApplicationStatus.SELECTED, officer, "Selected for SDE-1 offer"));

        // App 2: Rahul Sharma applied to Microsoft IDC (Interview scheduled)
        Application app2 = new Application(profile1, drive2);
        app2.setCurrentStatus(ApplicationStatus.INTERVIEW_SCHEDULED);
        app2.setRemarks("Qualified online assessment with 100% score.");
        applicationRepository.save(app2);

        statusHistoryRepository.save(new ApplicationStatusHistory(app2, null, ApplicationStatus.APPLIED, studentUser1, "Initial application"));
        statusHistoryRepository.save(new ApplicationStatusHistory(app2, ApplicationStatus.APPLIED, ApplicationStatus.SHORTLISTED, officer, "Profile verified"));
        statusHistoryRepository.save(new ApplicationStatusHistory(app2, ApplicationStatus.SHORTLISTED, ApplicationStatus.INTERVIEW_SCHEDULED, officer, "Round 1 interview scheduled"));

        // App 3: Rahul Sharma applied to Google (Shortlisted)
        Application app3 = new Application(profile1, drive1);
        app3.setCurrentStatus(ApplicationStatus.SHORTLISTED);
        app3.setRemarks("Online test cleared with rank 4.");
        applicationRepository.save(app3);
        statusHistoryRepository.save(new ApplicationStatusHistory(app3, null, ApplicationStatus.APPLIED, studentUser1, "Initial application"));
        statusHistoryRepository.save(new ApplicationStatusHistory(app3, ApplicationStatus.APPLIED, ApplicationStatus.SHORTLISTED, officer, "Shortlisted for technical interviews"));

        // App 4: Priya Patel applied to Amazon AWS (Under review)
        Application app4 = new Application(profile2, drive3);
        app4.setCurrentStatus(ApplicationStatus.UNDER_REVIEW);
        app4.setRemarks("Resume screening in progress.");
        applicationRepository.save(app4);
        statusHistoryRepository.save(new ApplicationStatusHistory(app4, null, ApplicationStatus.APPLIED, studentUser2, "Initial application"));

        // 7. Create Scheduled Interviews
        Interview interview1 = new Interview();
        interview1.setApplication(app2);
        interview1.setRoundName("Round 1 - Technical Coding & Problem Solving");
        interview1.setInterviewDate(now.plusDays(3).withHour(11).withMinute(0));
        interview1.setMode(InterviewMode.ONLINE);
        interview1.setMeetingLinkOrVenue("https://teams.microsoft.com/l/meetup-join/drive-msft-2025-rahul");
        interview1.setInstructions("Keep your camera enabled, prepare your IDE, and have your college ID ready.");
        interview1.setStatus(InterviewStatus.SCHEDULED);
        interviewRepository.save(interview1);

        Interview interview2 = new Interview();
        interview2.setApplication(app1);
        interview2.setRoundName("Round 2 - Googleyness and Leadership Fitment");
        interview2.setInterviewDate(now.minusDays(2).withHour(15).withMinute(0));
        interview2.setMode(InterviewMode.ONLINE);
        interview2.setMeetingLinkOrVenue("https://meet.google.com/abc-defg-hij");
        interview2.setInstructions("Discussion on collaborative projects, teamwork, and system design.");
        interview2.setStatus(InterviewStatus.COMPLETED);
        interview2.setFeedback("Excellent communication and technical maturity.");
        interviewRepository.save(interview2);

        // 8. Create Placement Offer
        PlacementOffer offer1 = new PlacementOffer();
        offer1.setApplication(app1);
        offer1.setStudent(profile4);
        offer1.setCompany(google);
        offer1.setDesignation("Software Development Engineer (SDE-1)");
        offer1.setPackageOffered(32.5);
        offer1.setOfferDate(LocalDate.now().minusDays(1));
        offer1.setJoiningDate(LocalDate.now().plusMonths(4));
        offer1.setStatus(OfferStatus.ACCEPTED);
        offer1.setOfferDetails("Full-time engineering offer including base salary, performance bonus, and stock grants.");
        offerRepository.save(offer1);

        // 9. Create Notifications
        notificationRepository.save(new Notification(studentUser1, "Interview Scheduled: Microsoft Azure",
                "Your Round 1 Technical Interview with Microsoft IDC is scheduled for " +
                        now.plusDays(3).toLocalDate() + " at 11:00 AM.", "/student/interviews"));

        notificationRepository.save(new Notification(studentUser4, "Congratulations on Your Google Offer!",
                "You have officially been offered the SDE-1 position at Google India with 32.5 LPA package!", "/student/offers"));

        notificationRepository.save(new Notification(studentUser2, "Application Received: Amazon AWS",
                "Your application for Cloud Support Associate at Amazon AWS has been submitted.", "/student/applications"));

        // 10. Audit Logs
        auditLogRepository.save(new AuditLog("admin@college.edu", "ROLE_ADMIN", "SYSTEM_INIT", "Database", "ALL", "System initialized with demonstration college dataset", "127.0.0.1"));
        auditLogRepository.save(new AuditLog("officer@college.edu", "ROLE_OFFICER", "CREATE_DRIVE", "PlacementDrive", drive1.getId().toString(), "Published Google SDE Drive", "127.0.0.1"));

        log.info("Demonstration database initialization finished successfully!");
    }

    private void initSampleResumeFolder() {
        try {
            Path dirPath = Paths.get(uploadDir);
            if (!Files.exists(dirPath)) {
                Files.createDirectories(dirPath);
            }
            Path sampleFile = dirPath.resolve("sample_resume.pdf");
            if (!Files.exists(sampleFile)) {
                // Create a valid minimalist PDF file
                String pdfContent = "%PDF-1.4\n1 0 obj\n<< /Type /Catalog /Pages 2 0 R >>\nendobj\n" +
                        "2 0 obj\n<< /Type /Pages /Kids [3 0 R] /Count 1 >>\nendobj\n" +
                        "3 0 obj\n<< /Type /Page /Parent 2 0 R /MediaBox [0 0 612 792] /Contents 4 0 R >>\nendobj\n" +
                        "4 0 obj\n<< /Length 55 >>\nstream\nBT /F1 18 Tf 50 700 Td (Sample Student Resume - College Placement Portal) Tj ET\nendstream\nendobj\n" +
                        "xref\n0 5\n0000000000 65535 f\n0000000009 00000 n\n0000000058 00000 n\n0000000115 00000 n\n0000000206 00000 n\n" +
                        "trailer\n<< /Size 5 /Root 1 0 R >>\nstartxref\n312\n%%EOF";
                Files.write(sampleFile, pdfContent.getBytes());
            }
        } catch (Exception ex) {
            log.warn("Could not create sample resume PDF: {}", ex.getMessage());
        }
    }
}
