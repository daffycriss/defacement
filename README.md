Defacement Monitoring Web Application

A full-stack Spring Boot–based Web Application designed to detect, monitor, and manage website defacement incidents in real time.

The system allows administrators to define defacement indicators (strings, image hashes, video hashes), assign them to monitored targets, and automatically scan websites at scheduled intervals. Alerts, logs, and dashboard summaries provide visibility into security status.

Features
========
Authentication & Authorization
Role-based access control (ADMIN, USER)
Secure login using Spring Security
JWT-based authentication for API endpoints
Password complexity validation
Change password functionality
Soft delete & restore users

Target Monitoring
=================
Create and manage monitored websites (targets)
Enable/disable scanning per target
Configurable scan interval
Soft delete support
Automatic scheduled scanning (cron-based)

Defacement Detection Engine
===========================
Supports multiple indicator types:
- STRING — Detects injected or malicious text
- IMAGE_HASH — Verifies image integrity via MD5 hash
- VIDEO_HASH — Detects unauthorized video references

Pluggable detection architecture via:
- IndicatorDetector interface
- IndicatorDetectionService
- Dedicated detector implementations per type

Dashboard
=========
Overview of all monitored targets
Status indicators:
- OK
- DEFACED
- ERROR
Real-time failed indicator count
Defaced indicator listing
Dashboard summary statistics:
- Total targets
- Healthy targets
- Defaced targets
- Failed targets
- Active alerts

Alerts
======
Automatically generated when defacement is detected
Linked to specific scan jobs
Severity levels
Acknowledgement support

Activity Logging
================
Login success/failure logging
Target and indicator changes
Scan lifecycle events
Defacement detection events
Aspect-based logging using custom @LogActivity annotation

Scheduler
=========
Automatic scan cycle using Spring @Scheduled
Prevents overlapping scan executions
Internet connectivity check before scan execution

Architecture Overview
=====================
Controller Layer
    ↓
Service Layer
    ↓
Detection Engine
    ↓
Repository Layer (JPA / Hibernate)
    ↓
Database

Core Components
===============
ScanOrchestratorService – Controls full scan cycle
TargetScanService – Handles individual target scans
IndicatorDetectionService – Delegates detection logic
DashboardServiceImpl – Aggregates dashboard data
LoViewerService – Centralized activity logging
DefacementScheduler – Scheduled execution

Technology Stack
================
Java 21+
Spring Boot
Spring Security
Spring Data JPA (Hibernate)
Thymeleaf
MySQL (configurable)
JWT (io.jsonwebtoken)
Maven

Roles
=====
ADMIN
- Manage users
- Create/edit/delete targets
- Manage defacement indicators
- View dashboard & logs
- Assign indicators to targets

USER
- View dashboard
- View target status
- Change password

Installation & Setup
====================
Clone Repository
git clone https://github.com/daffycriss/defacement.git
cd defacement

Configure Database Access
spring.datasource.url=jdbc:mysql://localhost:3306/defacement_db
spring.datasource.username=root
spring.datasource.password=yourpassword
spring.jpa.hibernate.ddl-auto=update

Build Project
mvn clean install

Run Application
mvn spring-boot:run

Application will start at:
http://localhost:8080

Scan Workflow
=============
Scheduler triggers scan every minute
Internet connectivity check is performed
Active targets are retrieved
For each target:
- Content is fetched
- Indicators are evaluated
- Results are stored
- Alerts created if needed
- Dashboard updates reflect latest status

Database Entities
=================
Key entities include:
User
Role
MonitorTarget
DefacementIndicator
TargetIndicator
ScanJob
ScanResult
Alert
ActivityLog
ConnectivityCheck

Security Highlights
===================
BCrypt password hashing
JWT token validation filter
Role-based endpoint protection (@PreAuthorize)
Soft-delete pattern for data integrity
Account protection against self-deletion

Future Improvements (Optional Roadmap)
======================================
Email/SMS alert integration
Multi-tenant support
Indicator regex support
Historical scan analytics
REST API documentation (Swagger/OpenAPI)
SIEM integration (e.g., QRadar)
Docker support
Horizontal scaling support
