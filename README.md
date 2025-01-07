# Exam Manager

## Overview
The Online School Exam Manager system is designed to enhance the process of conducting tests within an educational institution. It fits for the needs of students, teachers, and managers by providing specific functionalities to each role.
We utilized Java (OCSF server-clent framework, Greenrobot's EVENTBUS), JavaFX GUI, SQL, and Git.

## Features

### Role-based Access Control:
Students, teachers, and managers each have distinct roles with specific permissions.

### Student Functionality:
- Take tests online.
- View test results.

### Teacher Functionality:
- Create questions.
- Create tests.
- View statistical information.
- Request additional time for the test.
- Review and adjust test grades.

### Manager Functionality:
- View statistical information.
- Approve additional time requests.

### Online and Offline Testing:
- Tests can be taken online within the system.
- Option to download tests as Word files for offline completion, with submission functionality.

### Automatic Test Checks:
- Automatic test checks upon teacher's approval.

### Grade Adjustment:
- Teachers can modify test grades as necessary.
![hello student](https://github.com/yoav1255/High-School-Test-System/assets/101698622/94b2ce66-be3c-4187-9a7f-a55cb03cc618)
![hello teacher](https://github.com/yoav1255/High-School-Test-System/assets/101698622/2314f5ad-c3a3-44ab-99dc-63a9ff09a780)
![create question](https://github.com/yoav1255/High-School-Test-System/assets/101698622/d8ac8404-a238-4874-8bc8-b1b4a325a6d4)
![server](https://github.com/yoav1255/High-School-Test-System/assets/101698622/15c8d94a-5cf2-43d0-8af0-b0e1ec88bda7)
![Statistics](https://github.com/yoav1255/High-School-Test-System/assets/101698622/291cfe72-3941-4c42-aefa-40267b633b8d)



## Structure
Pay attention to the three modules:
1. **client** - a simple client built using JavaFX and OCSF. We use EventBus (which implements the mediator pattern) in order to pass events between classes (in this case: between SimpleClient and PrimaryController.
2. **server** - a simple server built using OCSF.
3. **entities** - a shared module where all the entities of the project live.

## Running
1. Run Maven install **in the parent project**.
2. Run the server using the exec:java goal in the server module.
3. Run the client using the javafx:run goal in the client module.
4. port : 3028 , host : (wifi ip address)
5. Press the button and see what happens!
