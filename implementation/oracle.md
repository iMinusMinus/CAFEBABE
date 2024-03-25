# Oracle

1. Products
   + Database
     + Oracle Database
       + __Zero Downtime Migration__ lets you quickly and smoothly move your Oracle databases to the Oracle Cloud or any Oracle Exadata Database Machine environment without incurring any significant downtime.
       + __Oracle Audit Vault and Database Firewall(AVDF)__ provides a comprehensive Database Activity Monitoring (DAM) solution that combines database audit logs with SQL traffic capture. 
         It monitors Oracle and non-Oracle database traffic to detect and block threats, improves compliance reporting by consolidating audit data from databases, operating systems, directories, and other sources.
       + __Oracle Data Safe__ delivers essential data security services for Oracle Databases, both in the cloud and on-premises, all through an accessible, easy-to-use cloud-based interface that requires no installation or deployment.
         + __Data Discovery__ helps you find sensitive data in your Oracle databases.
         + __Data masking__, also known as static data masking, is the process of permanently replacing sensitive data with fictitious yet realistic looking data.
       + The Oracle __Database Security Assessment Tool (DBSAT)__ is a stand-alone command line tool that accelerates the assessment and regulatory compliance process by collecting relevant types of configuration information from the database and evaluating the current security state to provide recommendations on how to mitigate the identified risks.
       + __Oracle Key Vault__ enables you to accelerate security and encryption deployments by centrally managing encryption keys, Oracle wallets, Java keystores, and credential files. 
         It is optimized for Oracle wallets, Java keystores, and Oracle Advanced Security Transparent Data Encryption (TDE) master keys. 
         Oracle Key Vault supports the OASIS KMIP standard. 
         The full-stack, security-hardened software appliance uses Oracle Linux and Oracle Database technology for security, availability, and scalability, and can be deployed on your choice of compatible hardware.
       + __Oracle Secure Backup__ provides a centralized disk, tape, and Cloud backup management for the entire IT environment.
     + Berkeley DB
     + Database Mobile Server

       Provides secure connections of embedded devices and mobile applications to Oracle Database. 
       Allows management applications, users, devices, and data on large deployments of mobile or remote devices.
     + MySQL
     + NoSQL Database

       Provides multi-terabyte distributed storage for key-value pairs, with scalable throughput and great performance.
     + Oracle Blockchain Platform Enterprise Edition

       Provides the industry's most comprehensive and enterprise-grade blockchain platform to securely extend your business processes and applications.
     + Rdb

       Provides a full-featured, relational database management system for mission-critical applications on OpenVMS platforms.
       Rdb是于1994年收购DEC公司Rdb部门而获得的关系型数据库产品。
     + Oracle __R Distribution__ is Oracle's free distribution of the open source R environment that has been enhanced for faster performance by taking advantage of hardware specific math library implementations.
     + TimesTen In-Memory Database

       A pure in-memory relational database that offers microsecond response time and extremely high throughput for online transaction processing (OLTP) applications. 
       Applications connect and access the database using industry-standard interfaces. 
       TimesTen also supports in-memory analytics and R programming.
   + Middleware
     + Application Testing Suite

       Acquire from Empirix E-TEST suite.

       ATS使用了EJB、JPA、JMS、JSP(OTM)、JSF(OLT)、flash/ActionScript3等技术，使用Weblogic部署。

       ```txt
       oats.ear
       ├─otm_ui.war
       ├─common_ejb.jar
       ├─admin_ui.war
       ├─oats_ui.war
       ├─otm_ejb.jar
       ├─olt_adfWeb.war
       ├─olt_adfModel.jar
       └─otm_ws_api.war
       ```
       + __Oracle Functional Testing__ formly known as Empirix E-Tester

         Oracle Functional Testing provides the easiest and fastest way to automate functional and regression testing for Web applications, Oracle packaged applications and Web services.  
         Its OpenScript integrated scripting platform enables users to create automated test scripts that simulate complex business transactions.

         ```txt
         OpenScript是基于Eclipse的IDE，它支持Adobe Flex、Web、HTTP、Web Service等类型的功能测试和负载测试，它自带了部分Oracle产品的功能测试和负载测试模块。
         OpenScript支持第一次迭代的初始化动作和最后一次迭代的结束清理动作，允许在每个迭代使用思考时间模拟用户停顿，每个迭代执行动作属于无参无返回。
         OpenScript提供了打开/关闭浏览器服务、模拟鼠标键盘动作触发服务、操作DOM服务、读写数据(csv/xml/json/db)服务。也支持用户提供的库复用。
         OpenScript可以设置不同范围的变量，保留一些前缀名，如script、obj、system、lib。
         测试数据源被称为databank，它可以是csv或txt文件，或者来自数据库。
         ```

       + __Oracle Load Testing__ formly known as Empirix E-Load

         Oracle Load Testing allows you to easily and accurately test the performance and scalability of your Web applications, Oracle packaged applications and Web services.

       + __Oracle Test Manager__ formly known as Empirix E-Manager.

         Oracle Test Manager is an easy to use tool that allows you to organize and manage your overall testing process.
       
         ```txt
         Requirements Management - provides the ability to define and manage requirements for a specific project. 
         You can specify details for each requirement, track the status of each requirement, and associate requirements with test cases to ensure testing coverage.
         
         Test Planning and Management - provides the ability to define and manage a test plan that incorporates both manual and automated test cases. 
         You can store Oracle Application Testing Suite scripts in the database, automatically execute scripts in Oracle OpenScript from the test plan interface, and automatically store the test results. 
         You can also associate requirements to test cases to ensure testing coverage, and associate test cases with issues so they can be reproduced and to keep track of how the issues were identified.
         
         Test Execution - provides the ability to create test sets, specify which tests to run and execute tests either manually or at a scheduled date and time.
         
         Defect Tracking - provides the ability to create and manage defects, referred to as issues, for a specific project. 
         You can associate test cases with issues so they can be reproduced and to keep track of how the issues were identified.
         
         Reporting - generates reports in standard HTML format for managing the overall testing process. 
         You can report on requirements, tests, and issues.
         
         Administration - provides and administration tool for entering and managing user accounts, project permissions, and general tool preferences.
         
         Custom Fields - provides the ability to add custom fields to the database for recording data specific to your projects.
         
         Database Repository - provides the ability to store test assets including test scripts, results, attachments, requirements, test plans, and defects in a common database.
         ```
     + Business Intelligence
       + __BI Server__: common enterprise business model and abstraction layer
       + __BI Answers__: ad-hoc query and reporting
       + __BI Interactive Dashboards__: highly interactive dashboards
       + __BI Delivers__: proactive business activity monitoring and alerting
       + __BI Publisher__: enterprise reporting and distribution of pixel-perfect reports
       + __Oracle Real-Time Decision Server__: predictive analytics for adaptive decision management
       + __Oracle Scorecard and Strategy Management__: strategic goal setting and tracking
       + __Oracle Data Visualization__: self-service visual analytics
     + __Crystal Ball__ is a spreadsheet-based application for risk measurement and reporting, Monte Carlo simulation, time-series forecasting and optimization. Crystal Ball provides a realistic and accessible way of modeling uncertainty enabling you to measure and report on the risk inherent in your key metrics.
     + __Endeca Server__ is a hybrid search-analytical database.
     + Enterprise Manager
       + __Oracle Management Agent__ is an integral software component that enables you to convert an unmanaged host to a managed host in the Enterprise Manager system. 
         The Management Agent works in conjunction with the plug-ins to monitor the targets running on that managed host.
       + __Oracle Management Service (OMS)__ is a Web-based application that orchestrates with the Management Agents and the plug-ins to discover targets, monitor and manage them, and store the collected information in a repository for future reference and analysis. 
         The OMS also renders the user interface for Enterprise Manager Cloud Control.
       + __Oracle Management Repository__ is a storage location where all the information collected by the Management Agent gets stored. 
         It consists of objects such as database jobs, packages, procedures, views, and tablespaces.
     + Fusion Middleware
       + Business Process Management(BPM)
       + __Coherence__ is the leading in-memory data grid solution that enables organizations to predictably scale mission-critical applications by providing fast access to frequently used data.
       + __Enterprise Data Quality (EDQ)__ is a comprehensive data quality management environment, used to understand, improve, protect, and govern data quality.
       + __Managed File Transfer(MFT)__ provides efficient transfer of documents of all sizes by consolidating disparate partner point solutions into a single deployment.
       + __Oracle Data Integrator(ODI)__ is a comprehensive data integration platform that covers all data integration requirements: from high-volume, high-performance batch loads, to event-driven, trickle-feed integration processes, to SOA-enabled data services.
       + __Oracle Stream Analytics__ allows for the creation of custom operational dashboards that provide real-time monitoring and analyses of event streams in an Apache Spark-based system.
       + TopLink
         
         Oracle TopLink is a mapping and persistence framework for use in a Java environment, including Java Platform, Standard Edition (Java SE) and Java Platform, Enterprise Edition (Java EE).
         The core functionality of TopLink is provided by EclipseLink, the open source mapping and persistence framework.
         TopLink产品为2002年收购TopLink公司而来。
       + Oracle Web Services Manager (OWSM)
       + __Oracle HTTP Server (OHS)__ is a web server based on Apache HTTP Server infrastructure and includes additional modules developed specifically by Oracle. Oracle HTTP Server can also be a proxy server. The features of single sign-on, clustered deployment, and high availability enhance the operation of the Oracle HTTP Server.
       + __iPlanet Web Server__, formerly known as Sun Java System Web Server or Sun ONE Web Server, delivers a secure infrastructure for hosting different web technologies and applications for enterprises.
       + __iPlanet Web Proxy Server__, formerly known as Sun Java System Web Proxy Server, Sun ONE Web Proxy Server, solves the problems of network congestion and that of slow response time, and provides control over network resources without burdening end users or network administrators.
       + __WebLogic Server (WLS)__ is the industry's best application server for building and deploying enterprise Java EE applications, a runtime platform for high performance and availability, and rich management tooling for efficient and low cost operations.
     + GlassFish Server
     + GoldenGate
     + __Identity Management(IdM)__ provides a unified, integrated security platform designed to manage user lifecycle and provide secure access across the enterprise resources, both within and beyond the firewall and into the cloud.
     + JRockit
       + JRockit JVM
       + JRockit Mission Control
     + Oracle GraalVM
     + __Real-Time Decisions (RTD)__ combines both rules and predictive analytics to power adaptive solutions for real-time enterprise decision management.
     + SOA Suite
       + Oracle Service Bus

         Oracle Service Bus is a configuration-based, policy-driven enterprise service bus designed for SOA life cycle management.
       + Oracle Business Process Execution Language (BPEL) Process Manager

         Oracle BPEL Process Manager provides a comprehensive, standards-based, and easy-to-use solution for assembling a set of discrete services into an end-to-end process flow to reduce the cost and complexity of process integration.
       + Oracle Business Activity Monitoring (BAM)

         Oracle BAM monitors business processes in real time to enable you to make informed tactical and strategic business decisions. 
         Unlike traditional reporting systems, Oracle BAM offers right-time operational intelligence for mission critical business processes. 
         Oracle BAM analyzes data before, during, and after business events.
       + Oracle Business Rules

         Oracle Business Rules enable dynamic business decisions at runtime, enabling you to automate policies, computations, and reasoning while separating rule logic from underlying application code. 
         This provides for agile rule maintenance and enables business analysts to modify rule logic without programmer assistance and without interrupting business processes.
       + Oracle Java EE Connector Architecture (JCA) adapters

         Oracle JCA adapters enable connectivity to virtually any data source inside the enterprise. 
         Oracle JCA adapters are standards-based and support both web services and JCA technologies.
       + Oracle B2B

         Oracle B2B enables an enterprise to exchange information electronically with a trading partners. 
         Oracle B2B supports a set of industry standards, including Electronic Data Interchange (EDI), UCCnet, RosettaNet, Chemical Industry Data Exchange (CIDX), Petroleum Industry Data Exchange (PIDX), Voluntary Interindustry Commerce Solutions (VICS), ebXML, and Universal Business Language (UBL).
       + Oracle SOA for Healthcare

         Oracle SOA Suite for Healthcare enables you to design, create, and manage applications that process health care data. 
         Oracle SOA Suite for Healthcare integration provides a web-based user interface in which to create and configure health care integration applications, and monitor and manage the messages processed through those applications.
       + Oracle Web Services Manager (OWSM)

         OWSM provides the policy manager for securing web services, including authentication and authorization.
     + Tuxedo
     + WebCenter
       + __WebCenter Content__ provides a single content management platform that helps companies consolidate assets with a unified set of tools. It enables organizations with a unified repository to house unstructured content and deliver it to business users in the proper format, and within context of familiar applications to fit the way they work.
       + WebCenter Portal
       + __WebCenter Sites__ is a Web Experience Management system. It helps you build desktop and mobile websites, personalize them with targeted content, gather feedback on their success, analyze visitor interactions with the website, and test changes to your website based on your visitors' preferences.
   + Applications
     + Commerce

       Oracle Commerce is an ecommerce platform that helps B2C and B2B businesses connect customer and sales data from their CRM to their financial and operational data so they can offer personalized experiences to buyers across sales channels.
     + Communications
       + Performance Reliability and Robustness (PRR) Tools
       + Performance Manager Tools
       + BRM Testing Tools
     + Policy Automation
     + User Productivity Kit (UPK/UPK Professional)
     + E-Business Suite
       + Order Management
         + Oracle Accounts Receivable Deductions Settlement
         + Oracle Advanced Pricing
         + Oracle Channel Rebates and Point of Sale Management
         + Oracle Channel Revenue Management
         + Oracle Configurator
         + Oracle E-Business Suite Customer Relationship Management
         + Oracle Incentive Compensation
         + Oracle iStore
         + Oracle Marketing
         + Order Orchestration and Fulfillment
         + Oracle Partner Management
         + Oracle Proposals
         + Oracle Quoting
         + Oracle Sales
         + Oracle Sales Contracts
         + Oracle Sales For Handhelds
         + Oracle TeleSales
         + Order Management
         + Oracle Trade Management
       + Logistics
         + Oracle Inventory
         + Oracle Landed Cost Management
         + Oracle Mobile Supply Chain Applications
         + Oracle Warehouse Management
         + Oracle Yard Management
       + Procurement
         + Oracle Contract Lifecycle Management for Public Sector
         + Oracle iProcurement
         + Oracle iSupplier Portal
         + Oracle Procurement Contracts
         + Oracle Purchasing
         + Oracle Service Contracts
         + Oracle Services Procurement
         + Oracle Sourcing
         + Oracle Spend Classification
         + Oracle Supplier Lifecycle Management
         + Oracle Supplier Network
       + Projects
         + Oracle Project Billing
         + Oracle Project Contracts
         + Oracle Project Costing
         + Oracle Project Planning and Control
         + Oracle Project Portfolio Analysis
         + Oracle Project Resource Management
       + Manufacturing
         + Oracle Configure to Order
         + Oracle Cost Management for Discrete Manufacturing and Inventory Logistics
         + Oracle Discrete Cost Management Information Discovery
         + Oracle E-Business Suite In-Memory Cost Management for Discrete Industries
         + Oracle E-Business Suite In-Memory Cost Management for Process Industries
         + Oracle E-Records
         + Oracle Flow Manufacturing
         + Oracle Manufacturing Execution System for Discrete Manufacturing
         + Oracle Manufacturing Execution System (MES) for Process Manufacturing
         + Oracle Master Production Scheduling
         + Oracle Mobile Supply Chain Applications
         + Oracle Outsourced Manufacturing
         + Oracle Process Manufacturing Process Planning
         + Oracle Process Manufacturing Product Development
         + Oracle Process Manufacturing Process Execution
         + Oracle Process Manufacturing Quality Management
         + Oracle Process Manufacturing Regulatory Management
         + Oracle Project Manufacturing
         + Oracle Shop Floor Management
         + Oracle Quality
         + Oracle Work in Process
       + Asset Lifecycle Management
         + Oracle Assets
         + Oracle Complex Maintenance Repair and Overhaul
         + Oracle Enterprise Asset Management
       + Service
         + Oracle Advanced Inbound Telephony
         + Oracle Advanced Scheduler
         + Oracle Depot Repair
         + Oracle Email Center
         + Oracle Field Service
         + Oracle iSupport
         + Oracle Mobile Field Service
         + Oracle Scripting
         + Oracle Spares Management
         + Oracle TeleService
       + Financials
         + Oracle Accounts Receivable Deduction Settlement
         + Oracle Cash and Treasury Management
         + Oracle Treasury
         + Oracle Procure-to-Pay
         + Oracle Travel and Expense Management
         + Oracle Credit-to-Cash
         + Oracle Advanced Collections
         + Oracle iReceivables
         + Oracle Loans
         + Oracle Credit Management
         + Oracle Financials Centralized Solution Set
         + Oracle Payments
         + Oracle Receivables
         + Oracle Financials Accounting Hub
         + Oracle General Ledger
         + Oracle Cash Management
         + Oracle Payables
         + Oracle Lease and Finance Management
         + Oracle Property Manager
       + Human Capital Management
         + Oracle Human Capital Management (HCM)
         + Oracle Talent Management
         + Oracle Workforce Management
         + Oracle Workforce Service Delivery
         + E-Business Suite Global Core Human Capital Management
     + PeopleSoft
       + Human Capital Management
       + Enterprise Resource Planning
       + Campus Solutions
       + PeopleTools and Technology
     + JD Edwards EnterpriseOne
       + Asset Lifecycle Management
         + Capital Asset Management
         + Condition-Based Management
         + Resource Assignments
         + Equipment Cost Analysis
         + Real Estate Management
         + Advanced Real Estate Forecasting
         + Rental Management
       + Financial Management
         + Accounts Payable
         + Accounts Receivable
         + Advanced Cost Accounting
         + Expense Management
         + Fixed Asset Accounting
         + General Ledger
         + Lease Accounting
         + Joint Venture Management
       + Human Capital Management
         + Human Resources Management
         + Payroll
         + Self Service Human Resources
         + Time and Labor
       + Project Management
         + Project Costing
         + Advanced Job Forecasting
         + Contract and Service Billing
         + Advanced Contract Billing
         + Homebuilder Management
         + Change Management
       + Agribusiness
         + Blend Management
         + Grower Management
         + Grower Pricing and Payments
       + Order Management
         + Sales Order Management
         + Fulfillment Management
         + Advanced Pricing
         + Customer Self Service
         + Agreement Management
       + Logistics
         + Apparel Management
         + Attribute Management
         + Demand Scheduling Execution
         + Inventory Management
         + Outbound Inventory Management
         + Warehouse Management
         + Transportation Management
       + Customer Relationship Management
         + Case Management
         + Service Management
       + Manufacturing
         + Requirements Planning
         + Manufacturing Management
         + Configurator
         + Quality Management
       + Supply Management
         + Buyer Workspace
         + Operational Sourcing
         + Procurement & Subcontract Management
         + Requisition Self-Service
         + Supplier Self-Service
         + Agreement Management
     + Siebel CRM
       + Sales
         + Account, opportunity, and territory management
         + Sales methodology coaching
         + Sales forecasting
         + Order management
         + Mobile selling
         + Partner relationship management (PRM)
       + Marketing
         + Campaign management
         + Email marketing
         + Events marketing
         + Marketing resource management
         + Web marketing
         + Loyalty marketing
       + Service
         + Contact center
         + Help desk
         + Field service
         + Warranty management
       + Commerce
         + Customer portals
         + Dynamic catalog
         + Dynamic pricer
         + Pricing analytics
         + Quote and order lifecycle management
         + Product and catalog management
     + Enterprise Performance Management
       + Oracle Data Relationship Management
         + Financial MDM
         + Analytical MDM
         + Data Governance
       + Oracle Hyperion Financial Close Management
       + Oracle Hyperion Financial Management
       + Oracle Hyperion Planning
   + IT Infrastructure
     + Solaris
     + Solaris Cluster
     + __Vdbench__ is a command line utility specifically created to help engineers and customers generate disk I/O workloads to be used for validating storage performance and storage data integrity.
     + VM Server
     + __VM VirtualBox__, formerly known as Sun xVM VirtualBox or Sun VirtualBox. 
     + __Oracle Linux__ is application binary compatible with RHEL.
       
       一个能与RHEL应用程序二进制兼容的Linux发行版。
   + Java
     + Java Card
     + Java EE
     + JRE
     + JDK
     + Java for Mobile
     + Java ME
     + Java SE
     + Java TV
   + Developer Tools
     + __Application Express (APEX)__ is the world's most popular enterprise low-code application platform that enables you to build scalable, secure web and mobile apps, with world-class features, that can be deployed anywhere – cloud or on premises.
     + __Developer Studio__, formerly known as Sun Studio.
     + JDeveloper & Application Development Framework (ADF)

       JDeveloper is a free integrated development environment that simplifies the development of Java-based SOA and Java EE applications.
       ADF is an end-to-end Java EE framework that simplifies application development by providing out-of-the-box infrastructure services and a visual and declarative development experience.
     + __NetBeans IDE__, formerly known as Sun NetBeans. 
       Oracle donated the source code of NetBeans to the Apache Software Foundation.
     + Oracle REST Data Services (ORDS)
     + Spatial Studio

       Spatial Studio, is a free tool that lets you connect with, visualize, explore, and analyze geospatial data stored in and managed by Oracle Spatial.
       Spatial Studio is a multiuser Java EE application that can be used as a standalone tool (Quick Start) or deployed to WebLogic Server.
     + SQL Developer

       SQL Developer is a free graphical tool that enhances productivity and simplifies database development tasks. 
       With SQL Developer, you can browse database objects, run SQL statements and SQL scripts, edit and debug PL/SQL statements, manipulate and export data, and view and create reports. 
       You can connect to Oracle databases, and you can connect to selected third-party (non-Oracle) databases, view metadata and data, and migrate these databases to Oracle.

       SQL Developer also integrates interfaces into several related technologies, including Oracle Data Miner, Oracle OLAP, Oracle TimesTen In-Memory Database, and SQL Developer Data Modeler (read-only).
     + SQL Developer Data Modeler

       Oracle SQL Developer Data Modeler is a data modeling and database design tool that provides an environment for capturing, modeling, managing, and exploiting metadata.

2. Industry Solutions
   + Automotive
   + Communications
   + Construction and Engineering
   + Consumer Packaged Goods
   + Education
   + Energy and Water
   + Financial Services
   + Food and Beverage
   + Government
   + Health
   + High Technology
   + Hospitality
   + Industrial Manufacturing
   + Life Sciences
   + Media and Entertainment
   + Oil and Gas
   + Professional Services
   + Public Safety
   + Retail
   + Travel and Transportation
   + Wholesale Distribution

## [NetSuite](http://netsuite.com)(1998~2016)

1. Products
   + NetSuite
     + ERP
       + Financial Management
         + Accounting

           Close With Confidence and Report Financials Quickly and Accurately
         + Fixed Asset Management

           End-to-End Lifecycle Management for Plant, Property and Equipment
         + Payment Management

           Drive Sales Safely with a Variety of Payment Methods
       + Supply Chain and Inventory Management
         + Demand Planning

           Optimize Inventory Management and Streamline the Supply Chain Process
         + Inventory Control and Warehouse Management

           Manage Every Stage of the Inventory Lifecycle
         + Purchasing and Vendor Management

           Gain Complete Visibility into the Procure-to-Pay Process
         + Manufacturing

           Optimize Your Manufacturing Processes On the Shop Floor
       + Procurement

         Optimize Company Savings with Better Spend Management
       + Order and Billing Management
         + Billing Management and Invoicing

           Improve Cash Flow with Efficient, Accurate Billing and Invoicing
       + Warehouse and Fulfillment

         NetSuite provides a seamless quote-to-order, processing-to-fulfillment solution
       + Revenue Recognition Management

         NetSuite's revenue recognition management solution helps companies comply with accounting standards and report financial results in a timely manner.
       + Financial Planning

         NetSuite's financial planning solution provides flexible, "what-if" financial modeling capabilities to help companies meet their budgeting and ongoing forecasting needs.
       + Human Capital Management
         + Core HRIS

           Organizations face many challenges in managing their people, such as managing a multi-generational and global workforce amidst a skills shortage.
         + Payroll Services

           Minimize Payroll Headaches, Reduce Tedious Paperwork, Get Peace of Mind
         + Incentive Compensation Management

           Accurately predicting, administering and tracking variable payouts is key to your corporate performance strategy and growth.
       + Recurring Revenue Management

         NetSuite's recurring revenue management solution integrates all of your front- and back-office processes for your subscription-based business.
     + CRM
       + Sales Force Automation
         + Sales Forecasting

           Sales forecasting can often be a mystery to your management team, but NetSuite CRM+ takes the guesswork out of forecasts with real-time sales data, complete visibility into opportunities, and a rich set of forecasting tools.
         + Customer Service Management
           + Customer Portal

             Customer Portal enables you to provide customers with highly personalized, interactive service on the Web.
         + Marketing Automation
           + Campaign Management

             Grow Your Company with End-to-End CRM Software in the Cloud
           + Web to Lead Forms

             NetSuite enables quick lead to opportunity conversion with integrated Web to Lead Form with CRM
         + Partner Relationship Management

           Drive Channel Ecosystem Collaboration with Integrated Partner Relationship Management
         + Mobile

           Drive Business Performance—Anytime, Anywhere, on Any Mobile Device
   + SuiteCommerce
     + Ecommerce
       + B2C Commerce

         The explosion of Internet-enabled devices means your products are never more than a click away from shoppers, wherever they are.
       + B2B Commerce

         SuiteCommerce gives B2B businesses the robust capabilities expected of an enterprise-class B2B ecommerce platform, including the ability to provide the same easy and information-rich shopping experience as a B2C website.
     + Point of Sale
     + Commerce Marketing
     + Order Management
     + Product Content Management
     + CRM
   + Professional Services Automation (PSA)
     + Project Management

       Visibility, Collaboration and Control to Drive On-Time Delivery
     + Resource Management

       Gain Complete Visibility into Resource Management and Skills
     + Project Accounting

       Drive Cash Flow by Automating Project Accounting and Invoicing
     + Timesheet Management

       Manage Timesheets to Match Your Business Needs
     + Expense Management

       Maximize Accuracy and Timeliness of Your Expense Processes
     + Analytics

       Get Actionable, Real-Time Visibility into Project Performance
   + OneWorld
     + Global ERP

       NetSuite OneWorld addresses the complex multinational and multicompany needs of midmarket organizations.
     + Global Ecommerce

       NetSuite OneWorld allows you to conduct ecommerce around the globe with multi-language, multi-currency, multi-country and multi-brand web stores that all can be run and managed from a single NetSuite system.
     + Global Service Resource Planning

       NetSuite OneWorld Services Resource Planning (SRP) streamlines the complete services lifecycle from marketing to project management, service delivery, billing and revenue management.
     + Global Business Intelligence

       NetSuite OneWorld provides real-time visibility across your entire enterprise, enabling unprecedented access to financial, customer and business data worldwide.
     + International Capabilities

       NetSuite helps companies manage global operations including multiple currencies, taxation rules and reporting requirements across geographies and subsidiaries, while providing real-time financial consolidation and visibility.
   + Service Resource Planning (SRP)

     A Complete Bid-to-Bill Lifecycle Solution
   + NetSuite OpenAir

     Leader in cloud professional services automation, stand-alone or integrated with existing enterprise systems.
   + Business Intelligence

     Real-Time Business Intelligence Across the Enterprise
2. Industries
   + Software/Internet Companies
   + Retail
   + Wholesale Distribution
   + Manufacturing
   + Professional Services
   + Advertising and Digital Marketing Agencies
   + IT Services
   + Media and Publishing
   + Financial Services
   + Consulting
   + Healthcare and Life Sciences
   + Energy
   + Nonprofit
   + Education

## [Ravello Systems](https://www.ravellosystems.com)(~2016)

1. TECHNOLOGY
   + __HVX__: Virtual infrastructure for the cloud

     HVX consists of three technology components and a management layer, wrapped up and offered as a SaaS.
     + Nested Virtualization
     
       High performance Binary Translation enables VMware/KVM VMs to run natively in any cloud
     + Overlay Network
     
       Software defined networking enables clean L2 networking in the cloud: static Ips, VLANs, routers, DHCP and more
     + Storage Overlay
     
       Ravello's solution abstracts native cloud storage and exposes local block devices directly to guest VMs
     + Management
     
       A highly available enterprise-grade system enable enterprises to deploy and manage complex applications in the cloud
2. SOLUTIONS
   + Dev/Test
   + Virtual Training
   + OpenStack
   + ESXi
   + Sales Enablement
   + Networking and Security Smart Labs

## [Taleo](http://taleo.com)(1999~2012)

1. PRODUCTS
   + __TalentReach™__ for building talent pools.
   + __Recruiting™__ for selecting and hiring the best talent.
   + __Assessment™__ for improving quality of hire and retention.
   + __Onboarding™__ for getting new hires productive fast.
   + __Performance™__ for measuring and managing talent every day.
   + __Goals™__ for aligning talent to business goals.
   + __Succession™__ for finding new leaders and planning for the future.
   + __Compensation™__ for rewarding top performers.
   + __Development™__ for improving workforce quality.
   + __Taleo Learn™__ is the leading employee development and training software solution that supports the learning challenges of large complex enterprises.
   + __Analytics™__ to gain powerful Talent Management insights.
   + __Connect™__ to build fast, affordable and reliable integrations.
   + __Passport™__ for plug and play integration to certified Taleo partners.
   + __Anywhere™__ for anytime, anywhere access to Taleo.

## [RightNow Technologies](http://rightnowtech.com)(1997~2011)

1. RightNow CX
   + RightNow Web Experience

   Deliver a branded web experience and always-available access to knowledge and subject matter experts, empowering customers to research, purchase products, and resolve issues online with or without agent assistance.
   + RightNow Social Experience

   Monitor and respond to what customers are saying about you on the social web, and build your own community for support, innovation, brand affinity, and more.
   + RightNow Contact Center Experience

   Deliver superior customer experiences consistently across multi-channel interactions, maximizing agent productivity, lowering costs, and driving revenue.
   + RightNow Engage

   Facilitate seamless, personalized customer experiences through proactive engagement, actionable customer feedback, and analytics that provide deep business insight.
   + RightNow CX Cloud Platform

   Rapidly deploy, integrate, extend, and knowledge-enable RightNow’s suite of CX applications.

## [Endeca](http://www.endeca.com)(~2011)

1. PRODUCTS
   + Endeca Latitude
     + __Latitude Studio__ – A highly-interactive, component-based environment for building and deploying enterprise-class analytic applications powered by the Endeca MDEX Engine.
     + __The MDEX Engine__ – a hybrid search-analytical database designed for agile BI.
     + __Latitude Information Integration Suite__ – A powerful platform with a high-performing ETL tool, system connectors, and content enrichment libraries for unifying diverse information, including structured data and unstructured content.
       + __Latitude Data Integrator__ – a complete ETL environment for integrating and enriching enterprise data and content
       + __Latitude Content Acquisition System (CAS)__ – a collection of connectors for extracting, enriching,  and integrating unstructured content from network file systems, Web sites, and content management system (CMS) repositories
       + __Open Interfaces and Connectors__ – allows direct data integration from standard commercial Extract, Transform, and Load (ETL) tools, including Informatica PowerCenter
   + Endeca InFront
     + __Page Builder__ provides a single, flexible platform  from which to create, deliver, and manage content-rich, multichannel customer experiences. 
       Page Builder allows non-technical users to deliver targeted, user-centric experiences in a scalable way - creating always-relevant customer experiences that drive conversion rates and accelerate cross-channel sales.
     + __Mobile__ powers fully-featured customer experiences for the mobile Web and iPhone, iPad, and Android apps. 
       Tightly integrated with existing backend technology and merchandising tools, InFront Mobile provides a single platform for business users to deliver brand consistency in a best-in-class mobile experience.
     + __Social__ allows your business to integrate with Facebook to enhance the customer experience across channels and capture additional sales with a transactional Facebook storefront. 
       InFront Social features tight integration with your backend technology and Endeca merchandising tools, making it easy to deliver and optimize personalized experiences wherever Friends browse, share, and buy.
     + __SEO__ optimizing your Website’s content and architecture for the major web search engines
     + __Faceted Search & Navigation__ features best-in-class faceted search and guided navigation leveraging Endeca’s MDEX engine technology that offer out-of-the box components that build the foundation for a wide range of best-in-class customer experiences.
     + __Intelligence__ brings Endeca’s advanced Business Intelligence product – Latitude - to the eBusiness world, giving teams the ability to search through one easy-to-use application, to explore all information and answer new, unanticipated questions.
2. SOLUTIONS
   + Business Intelligence
   + Customer Experience Management
     + B2C eCommerce
     + B2B eCommerce
     + Site Search
     + Online Media
   + Government


## [Art Technology Group](http://atg.com)(1991~2010)

1. Products
   + ATG Commerce
     + Platform
       + Shoping Cart and Catalog
         + Commerce
         + B2B Commerce
           + Manage customer accounts effectively
           + Enable approval workflows
           + Support multiple payment types and options
           + Personalize your B2B storefront
           + Increase sales by helping customers find the right products quickly
           + Easily manage multiple sites and brands
           + Engage visitors directly
           + Empower your business users
           + Manage your online business efficiently
           + Manage your business more effectively with powerful business reporting
       + Personalization Engine
         + __ATG Adaptive Scenario Engine__ is e-commerce software that provides the technology and core functionality to enable you to develop and manage robust, adaptable, scalable, and personalized e-commerce software applications across channels and through the complete customer lifecycle. 
           The software allows you to easily integrate these e-commerce software applications across your marketing, merchandising, e-commerce, and customer care organizations.
       + Content Management
         + __ATG Content Administration__ is a comprehensive web content management solution that supports personalized websites throughout the entire content process including creation, version tracking, preview, editing, revision, approval, and site deployment.
     + Applications
       + Merchandising and Marketing
         + __ATG Merchandising__ enables your merchandising professionals to directly manage your online storefront, including catalogs, products, search facets, promotions, pricing, coupons, and special offers, to help quickly connect shoppers with the items most likely to interest them.
         + __ATG Search__ is a dynamic, integrated search solution that incorporates natural language technology into your storefront.
           It enables shoppers to quickly and efficiently navigate your commerce site to find merchandise they want and discover new items, as well as make purchases directly from the search results page.
         + __ATG Outreach__ leverages customer information gained through web interactions, preferences, and behaviors so you can develop an effective website marketing strategy and create relevant, personalized outbound marketing and service campaigns, including email and web campaigns.
       + Customer Service
         + __ATG Commerce Service Center__ is a web-based contact center application for order administration, sales support, and customer care. 
           It enables your sales and service agents to more efficiently help customers over the telephone, on the website, or via email, using ATG's industry-leading personalization capabilities.
         + __ATG Knowledge__ streamlines and improves customer service, combining knowledge management with customer and incident management into a single solution.
         + __ATG Self-Service__ empowers your customers to answer questions and complete transactions through highly personalized web self-service. 
           It combines an answer repository with multi-lingual natural language search and navigation capabilities in a single solution.
       + Analytics and Testing
         + __ATG Campaign Optimizer__ enables you to evaluate and improve your e-commerce initiatives.
         + __ATG Customer Intelligence__ is an integrated set of datamart and reporting capabilities that enable you to monitor and analyze commerce and customer care performance. 
           It combines key data from the ATG product suite, such as purchases, searches, escalations, and click-throughs, with behavioral data from web traffic analysis and demographic data, such as age, gender, and geography.
   + ATG Optimization
     + Live Help
       + __Optimize cross-channel customer service__ by connecting the right online customers with the right live agents through the right communication channel at the right time
       + __Click to Call__ is an award-winning live help application that helps you deliver smart online voice assistance by engaging the right online visitors at the right time, connecting them with the best available agents, and delivering faster, more personal, more interactive voice assistance.
       + __Click to Chat__ is an interactive live chat service that lets site visitors communicate with agents for assistance with research, making a purchase, or obtaining service.
       + Email Response
     + Lead Performance
       + __Optimize cross-channel, direct-response marketing, and advertising campaigns__ by converting, tracking, recording, and measuring voice leads
       + __Call Tracking__ enables media companies and interactive marketers to monitor and measure cross-channel advertising performance.
       + Click to Call
       + Click to Chat
       + Form to Call
       + Save and Send
       + Video Connect
       + Winback

         ATG Winback helps you "win back" customers who don't initially complete an online transaction.
     + Recommendations
       + __Optimize cross-channel merchandising__ by predicting shoppers’ intent in each visit and automatically recommending the most relevant, profitable products from your online catalog
       + __Recommendations__ is an automated, predictive recommendation engine that helps you quickly lift revenue by recommending the most relevant products from the catalog to each shopper.
       + Email Recommendations

         
   + __ATG Catalyst__ enables customers and partners to access and exchange code, insights, and extensions to ATG Commerce Suite.
2. Industry Solutions
   + Retail
   + Telecom
   + Consumer Product Manufacturing
   + Financial Services
   + Travel
   + Media and Entertainment
   + Distribution / Wholesale
   + High Tech
   + Consumer and Business Services

## Secerno(~2010)

1. DataWall

   Secerno DataWall protects as if it were a database firewall, monitoring data access and logging or alerting anomalies, it does not matter whether database requests come from outside the organization, disgruntled employees or other internal sources – including direct access to the database by privileged users such as Database Administrators.
  + __database activity monitoring (DAM)__ mode detects and logs out-of-policy activity and produces alerts, but does not directly block potential threats.
    Logging and alerting can be fine-tuned to specific types of SQL statement that reflect how applications interact with databases.
  + real-time data protection
  + data auditing and compliance

## [Sun](http://www.sun.com)(1982~2009)

1. Products
  + Software
    + Application & Integration Services
      + Sun Java Composite Application Platform Suite (JavaCAPS)
        + Sun Enterprise Service Bus Suite(Sun SEB Suite)
          + __Sun Enterprise Service Bus__ - pluggable integration platform.
          + __Sun Business Process Manager__ - orchestrates the flow of activities across any number of partners.
          + __Sun Adapters__ - accelerate integration with legacy applications, packaged applications, and data stores.
          + __Sun GlassFish Enterprise Server__ - flexible application server ideal for service-oriented architectures and rich Internet applications.
          + __Sun Java System Portal Server__ - enables easy portal creation for collaboration.
          + __Sun Java System Web & Web Proxy Servers__ - provides a single Web deployment platform.
          + __Sun Java System Directory Server, Enterprise Edition__ - provides a centralized data repository.
          + __Sun Java System Access Manager__ - enables authentication and authorization management.
          + __NetBeans Integrated Development Environment__ - provides a single interface for building, testing, and deploying reusable, secure Web services, composite applications, and business processes.
        + Sun GlassFish ESB

          GlassFish Enterprise Service Bus (ESB) is a lightweight and agile ESB platform that packages the innovation happening with Project Open ESB, the GlassFish application server, and the NetBeans IDE into a commercially supported, enterprise-class platform.
        + Sun Master Data Management Suite(Sun MDM Suite)
          + __Sun Master Index__ - manages redundant data within disparate systems to uniquely identify common records and build a cross index for a single view.
          + __Sun Data Integrator__ - optimized to extract, transform, and load (ETL) bulk data between files and databases.
          + __Sun Enterprise Service Bus__ - provides the foundation for application integration.
          + __Sun Business Process Manager__ - provides ability to model, test, implement, monitor, manage, and optimize business processes.
          + __Sun Java System Access Manager__ - delivers authentication and policy-based authorization.
          + __Sun Java Directory Server Enterprise Edition__ - provides a central repository for storing and managing identity profiles.
          + __Sun Java System Portal Server__ - provides easy portal creation for personalizing content for users.
          + __Sun Adapters Bundle__ - accelerate integration with legacy applications, packaged applications, and data stores.
          + __GlassFish Enterprise Server__ - is an application server compatible with Java Platform, Enterprise Edition, for developing and delivering server-side applications.
          + __NetBeans Integrated Development Environment (IDE)__ - provides the single interface for building, testing, and deploying reusable, secure Web services.
      + __Sun GlassFish Communications Server__  is a Java EE technology-based converged application server combining enterprise service-oriented architecture (SOA) and Web services capabilities with Session Initiation Protocol (SIP) servlets.
      + Sun GlassFish Enterprise Server

        The Sun GlassFish Enterprise Server (formerly Sun Java System Application Server) is a comprehensive support offering for GlassFish, the leading open-source and open-community platform for building and deploying next-generation applications and services.
      + Sun GlassFish Portfolio
        + Sun GlassFish Enterprise Server
        + Sun GlassFish Enterprise Service Bus (ESB)
        + Sun GlassFish Web Space Server

          A new class of portal functionality for simplifying collaboration and the development of Web content.
        + Sun GlassFish Web Stack

          An integrated stack of popular open-source, Web-tier infrastructure technologies such as Apache HTTP server, MySQL, memcached, PHP, and Ruby optimized for the Solaris, OpenSolaris, and Linux platforms.
      + __Sun GlassFish Message Queue__ is a leading business integration messaging service that provides exceptional reliability and scalability for both large and small-scale deployment environments.
    + Application Development
      + NetBeans

        NetBeans is an award-winning open-source Integrated Development Environment (IDE) and Platform available for Windows, Mac, Linux, and Solaris that lets developers rapidly create web, enterprise, desktop, and mobile applications with Java, C/C++, JavaScript, Ruby, Groovy, and PHP.
        It is supported by a vibrant developer community and offers a diverse selection of third-party plugins.
        NetBeans is a must-download for software developers.
      + Sun Studio

        Provides corporate developers and ISVs with a comprehensive, integrated suite of tools for the development and deployment of enterprise C, C++, and Fortran applications on Sun platforms.
      + SDKs: Directory Server SDK, Java 2 SDK, Standard Edition, Java Application Platform SDK, Java Dynamic Management Kit, Java EE SDK, Solaris WBEM Services, Solstice Enterprise Agents SDK, StarOffice SDK, WBEM SDK
    + Collaboration & Communication
      + Calendar Server

        A high performance, Internet standards-based calendar server for service providers and large enterprises
        Through a web UI or a connection to Microsoft Outlook, Thunderbird/Lightning or Evolution, Calendar Server provides personal calendaring and group scheduling to consumers at home or at work and integrates with mail and address book functionality
      + Instant Messaging

        Sun Java System Instant Messaging provides a scalable and reliable Java technology-based client and server for secure real-time communications and presence management.
      + Sun Java Communications Suite
        + Calendar Server
        + Instant Messaging
        + Indexing and Search
        + Messaging Server
        + Mobile Communications
        + Convergence

          An elegant and powerful AJAX Web client that provides a rich mash up of email, calendaring, contacts, presence, instant messaging, and other Web services.
        + Connector for Microsoft Outlook

          Enables the use of Outlook as a desktop client on Windows with Sun Java System Messaging and Calendar Servers.
        + Communications Sync

          Synchronize data to a wide range of devices even when Internet access is not available.
      + Java Indexing and Search Service

        The Sun Java Indexing and Search Service provides an indexing and search capability for the Sun Java Communications Suite, enabling fast, efficient email content search from a wide range of clients.
      + Messaging Server

        The Sun Java System Messaging Server, a component of the Sun Java Communications Suite, enables enterprises and service providers to provide secure, reliable messaging services for entire communities of employees, partners, and customers.
      + Mobile Communications

        Sun Java Mobile Communications enables over-the-air (OTA) synchronization support for multiple mobile devices with calendar and contact data stored on Sun Java Communications Suite servers.
    + Databases
      + __MySQL__ database is the world's most popular open source database because of its fast performance, high reliability, ease of use, and dramatic cost savings.
      + __Java DB__ is Sun's supported distribution of the open source Apache Derby database
      + PostgreSQL for Solaris
    + Desktop
      + ODF Plugin 1.1 for Microsoft Office

        The Sun ODF Plugin for Microsoft Office gives users of Microsoft Office Word, Excel and PowerPoint the ability to read, edit and save to the ISO-standard Open Document Format (ODF).
      + StarOffice

        StarOffice Software is the powerful, affordable, and comprehensive office productivity suite (word processor, spreadsheet, presentation tool, database, drawing program) that runs on Solaris OS, Windows, Macintosh OS X and Linux.
        Fully compatible with MS Office, it includes a built-in PDF export and supports XML, Flash and HTML.
      + __OpenOffice__ is a multi-platform open source project that forms the core of StarOffice
      + StarOffice Server

        Server based PDF document creation -- fast, flexible and reliable.
      + Sun Ray Clients
      + __Sun Secure Global Desktop Software__ uncouples applications from their underlying infrastructure and delivers the applications through a virtualized desktop environment to existing desktop systems, laptops, Sun Ray clients, other thin clients and mobile devices.
    + Enterprise Computing
      + Solaris Cluster

        Delivers application services to the data center or enterprise; enables the use of core Solaris services across a tightly coupled cluster, while maintaining full Solaris Operating Environment compatibility for existing applications.
      + Solaris Cluster Geographic Edition

        Solaris Cluster Geographic Edition is a multi-site, multi-cluster disaster recovery solution that manages the availability of application service and data across geographically dispersed Solaris Clusters.
        In the event that a primary Solaris Cluster goes down, Solaris Cluster Geographic Edition enables sysadmins to start up the business services with replicated data on the secondary Solaris Cluster.
      + Netra High Availability Suite

        Provides a suite of foundation services that enable the deployment of applications on a highly available Solaris platform; can be embedded in a solution to augment standard Solaris availability for a cluster of distributed, loosely coupled nodes, or to complement an existing, customer-provided HA framework.
      + Grid Engine

        Optimizes system utilization by automatically distributing workloads across networked resources based on business policies.
      + HPC ClusterTools

        Perform resource management and system administration; develop parallel applications.
      + HPC Software, Linux Edition

        Provides all the components needed to turn bare-metal systems into a running HPC cluster.
      + __Java Enterprise System__ is a comprehensive set of subscription-based services that combines software, support, professional services, and educational services in a single package, for a single price.
      + Mainframe Batch Manager

        Sun Mainframe Batch Manager software provides the administration, execution and management of a mainframe batch workload on Sun servers along with the facilities that allow the integration of third-party system management software components.
        In addition, Sun MBM software provides tools for automatically translating z/OS, MVS and VSE JCL Job streams to the Sun Mainframe Batch Manager environment.
      + Mainframe Transaction Processing

        Sun Mainframe Transaction Processing (MTP) provides a cost effective alternative to mainframe CICS.
        Sun MTP provides a low cost high performance CICS transaction processing environment on Sun servers supporting Cobol, PL/1, C and Java applications accessing either VSAM or relational data files.
    + Identity Management
      + Sun Java System Directory Server Enterprise Edition
        + __Directory Server__ for core directory service
        + __Directory Proxy Server__ for load-balancing, high-availability, virtualization, and distribution capabilities
        + __Identity Synchronization for Windows__ for identity data, password, and group synchronization between Microsoft Active Directory and Java System Directory Server
        + __Directory Editor__ for directory object editing
        + __Directory Server Resource Kit__ for tuning and optimizing directory server performances
      + Identity Compliance Manager

        Identity Compliance Manager helps companies streamline operations, enhance compliance, and reduce costs by integrating and automating access certification and separation of duties (SoD) policy enforcement across the enterprise and extranet.
        Identity Compliance Manager is a cost-effective solution for getting your compliance projects off the ground.
      + __Identity Manager__ combines provisioning and auditing to prevent and detect compliance violations.
      + __Role Manager__ formerly Vaau RBACx.

        Sun Role Manager provides comprehensive role lifecycle management and identity compliance capabilities to streamline operations, enhance compliance, and reduce costs.
      + OpenSSO Enterprise

        Sun OpenSSO Enterprise (formerly Sun Access Manager and Sun Federation Manager) is the single solution for Web access management, federation, and Web services security.
    + Java
      + Java Platform, Enterprise Edition (Java EE)
      + Java Platform, Standard Edition (Java SE)
      + Java SE for Business

        Java SE for Business is a new product based on Sun's Java SE that offers customers faster access to critical fixes, a longer roadmap for support, and enterprise features designed to reduce the cost of deployment.
      + Java Real-Time System

        When critical functions require precise, predictable execution - then Java Real-Time System is the right choice.
        Strict priority enforcement, a real-time garbage collector, and other features enable developers unparalleled control over their Java environment and their application.
        For applications ranging from robotic control to defined sub-millisecond response times - Java Real-Time System is right for you.
      + Java Platform, Micro Edition (Java ME)
      + Java Card Technology
      + JavaFX
      + JavaFX Mobile
    + Operating Systems
      + Solaris

        Sun OS基于BSD Unix，运行在SPARC(Scalable Performance Architecture)硬件上，为兼容Intel CPU从5.0版本转向基于Unix System V Release 4后，改名为Solaris，其开源版本OpenSolaris已停止维护。
      + OpenSolaris
    + Systems Management
      + Sun Connection

        A multi-platform IT infrastructure management platform for integrating and automating management of thousands of heterogeneous systems as a single system.
      + Sun N1 Service Provisioning System

        N1 Service Provisioning System enables IT to accelerate application deployment across heterogeneous environments in a consistent manner.
      + Sun Management Center

        Sun Management Center provides in-depth monitoring and management capabilities for your Sun enterprise servers.
        With support for advanced Solaris 10 features, including Solaris Containers and DTrace, you can significantly raise the service quality and maximize the ROI at the same time.
    + Virtualization
      + Sun xVM Server

        Sun xVM Server is a reliable bare-metal hypervisor that is simple to install and use.
        Run unlimited Windows, Linux and Solaris guests using open source virtualization.
      + Sun xVM Ops Center

        The highly scalable, unified management platform for physical and virtual environments.
        Use Sun xVM Ops Center to manage multi-platform x64 and SPARC systems distributed throughout a global datacenter and integrate with existing toolsets.
        Ready to facilitate many aspects of compliance reporting (ITIL) and datacenter automation, Sun xVM Ops Center enables management of thousands of systems simultaneously.
      + Sun xVM VirtualBox

        Sun xVM VirtualBox supports any operating system as a guest OS, giving you the flexibility to develop applications on your platform of choice.
        Which means you can run Windows in a virtual machine on a Mac or Linux platform, or run Solaris OS and Windows applications right alongside Apple applications on your MacBook.
      + Sun Virtual Desktop Infrastructure Software(Sun VDI)
  + Servers
    + Blade Servers
    + x64 Servers
    + CoolThread Servers
    + Netra Carrier Grade Systems
    + SPARC Enterprise Servers
    + Entry Rackmount Servers
    + Mid-Range Servers
    + High-End Servers
  + Storage
    + Tape Storage: Tape Libraries, Tape Virtualization, Tape Drives, Tape Encryption, Tape Media
    + Disk Storage: Datacenter Disk, Unified Storage, Storage Servers, Modular Disk, Storage Expansion Array, Workgroup Disk
    + Open Storage: Unified Storage, Storage Servers, Storage Expansion Array
    + Storage Networking: Host Bus Adapters, Switches and Directors, Extension Products, Blades and Modules
  + Networking
    + Ethernet
    + Fibre Channel
    + InfiniBand
  + Desktop Systems
    + Desktops & Workstations
    + Sun Ray Clients
    + Monitors
    + Peripherals
    + Graphics Cards
  + Microelectronics
    + UltraSPARC

2. Solutions
  + Cloud Computing
    + Virtualization: Hypervisor (xVM Server), OS (Solaris Containers and Zones), Network (Crossbow), Storage (COMSTAR, Solaris ZFS), Applications (GlassFish, Java CAPS)
    + The Sun Modular Datacenter (SunMD)
    + The Sun Constellation System
    + Open Storage
    + Open Database
  + Eco-Efficient Computing
  + High Performance Computing
    + Sun Constellation System
    + Sun Compute Cluster
    + Sun Storage Cluster
    + Storage and Archive
    + Sun Unified Storage Solutions
    + Sun Visualization System
  + Enterprise Business
  + Identity Management
  + Industries
    + Communications
    + Education
    + Energy
    + Financial Services
    + Government
    + Healthcare
    + Life Sciences
    + Media & Entertainment
    + Retail Trade
    + Transportation and Travel
  + Open Storage
  + Small and Medium Business
  + Virtualization
    + Desktop Virtualization: Sun Virtual Desktop Infrastructure (VDI) Software, Managed Virtual Desktop Solution, Sun xVM VirtualBox
    + Server Virtualization: Sun xVM Server, Solaris Containers, VMware Infrastructure, Sun Fire x64 Servers, Sun CoolThreads Servers, Sun Blade Modular Systems, Sun SPARC Enterprise Servers, Logical Domains (LDoms)
    + Storage Virtualization: Primary/Disk Storage, Tape Storage, Storage Virtualization Services
    + Virtualization Management: Sun xVM Ops Center, Solaris Operating System
  + Web Infrastructure
    + Build: Solaris + AMP, NetBeans, GlassFish
    + Optimize
    + Protect: Sun Message Security Solution for Brightmail, Sun Web Server Encryption Server

### [LongView Technologies LLC](http://animorphic.com)(1994~1997)

LongView Technologies LLC (doing business as Animorphic Systems in California) is a Palo Alto-based startup company.

1. Hotspot VM

### Forte(~1999)

an enterprise software company specializing in integration solutions, to round out its portfolio.

### [iPlanet](http://www.iplanet.com)(1999~2002)

i-Planet was acquired by Sun Microsystems In October, 1998.

Netscape, Sun, and AOL - formed iPlanet E-Commerce Solutions, a Sun-Netscape Alliance in March 1999 to build, market, and service e-commerce infrastructure solutions.

On March 17, 2002, Sun officially concluded its original Alliance agreement with AOL.
iPlanet is now a division of Sun and is a core component of the Sun™ Open Net Environment (Sun ONE).

1. Products
  + Commerce Services
    + iPlanet BillerXpert

      iPlanet BillerXpert is a comprehensive Internet bill presentment and payment (IBPP) solution that allows an enterprise to provide customer convenience, build customer loyalty, manage customer relationships, and generate new revenue opportunities.
    + iPlanet BuyerXpert

      iPlanet BuyerXpert, a component of the comprehensive Sun-Netscape Alliance Strategic Internet Procurement solution, streamlines internal processes and provides centralized control in the acquisition of goods and services required for enterprise operations.
    + iPlanet Market Maker

      iPlanet™ Market Maker is standards-based software that helps enterprises and service providers implement effective e-marketplaces and dynamic commerce in a many seller to many buyer environment.
      The product provides the capability to operate effective trading exchanges, with such essential functionality as the ability to aggregate supplier catalogs, implement multiple pricing models, participate in online negotiations, conduct forward and reverse auctions, run exchanges, customize and deliver content to participants, and provide membership management capabilities.
    + iPlanet SellerXpert

      iPlanet™ SellerXpert software helps enterprises and service providers create web-based sales channels that increase revenue, lower the costs of acquiring and retaining customers, and build customer loyalty.
      The application provides customizable business policies that enable enterprises to tailor pricing, shipping, billing, payments, and other terms and conditions required by even the most complex buyer-seller trading agreements.
    + iPlanet Trustbase Transaction Manager
  + Portal Services
    + iPlanet Portal Server

      The iPlanet™ Portal Server is the industry's first full-service platform for deploying robust e-commerce portals.
      iPlanet Portal Server provides all of the membership management, personalization, aggregation, security, integration, and search services needed to quickly deploy today's most demanding business-to-employee, business-to-consumer, and business-to-business portals.
      Companies can now roll out highly scalable, highly secure portals far more rapidly- even plugging in best of breed applications from other software vendors.
  + Communication Services
    + iPlanet Calendar Server

      iPlanet™ Calendar Server is software that enables users to manage schedules, share resources, and schedule events or appointments.
      With the intuitive, Web-based interface of the iPlanet Calendar Server, end users can access their calendars anytime, anywhere from any Web-enabled device.
      It also allows users and administrators to customize the product to meet their needs.
      Customers in both enterprise and service provider markets use iPlanet Calendar Server in conjunction with iPlanet Messaging Server to offer their users a comprehensive communications and collaborative environment.
    + __iPlanet Messaging Server__ provides a highly scalable, reliable Web-based solution for communications and messaging that integrates the Sun™ Internet Mail Server (SIMS) and Netscape Messaging Server (NMS).
    + __Netscape Messaging Server__ offers a scalable, carrier-grade platform for Internet-based messaging at one of the industry's lowest total cost of ownership.
    + __Sun Internet Mail Server__ offers a scalable, carrier-grade platform for Internet-based messaging at one of the industry's lowest total cost of ownership.
    + iPlanet™ Wireless Server
  + Application & Integration Services
    + __iPlanet™ Unified Development Server__ (formerly Forte™ 4GL) is a complete and integrated solution for the rapid creation, deployment and management of networked applications.
    + __iPlanet Application Builder__ has been renamed the iPlanet™ Developer Pack, Enterprise Edition
    + __iPlanet Application Server__ maximizes application re-use and developer collaboration and demonstrates the potential of leveraging Java for large-scale web and wireless applications. 
      + Netscape Application Server (NAS)

        Application Server for Large-Scale Business Applications
      + Sun NetDynamics

        Delivers Scalable Support for Large Web Portals
    + iPlanet Developer Pack Enterprise Edition
    + iPlanet Integration Server
    + __iPlanet Message Queue for Java__  is a Message Oriented Middleware (MOM) product.
      Originally named Java Message Queue (JMQ), iPlanet Message Queue for Java is a standalone, production implementation of the Java Message Service specification.
    + __iPlanet Process Builder__ is a visual process automation tool used to create applications that automate business processes for deployment on the iPlanet Application Server.
    + iPlanet Web Servers
      + iPlanet Web Server Enterprise Edition

        High-Performance Web Server for E-Commerce
      + Netscape FastTrack Server

        Web Server for Developers and Small Workgroups
  + Unified User Management
    + __iPlanet Certification Management System__

      Scalable Certificate-Based Security Solution
    + __iPlanet Meta-Directory__ provides a unified view of a user's accounts and profile information and automates the application of business rules across the enterprise.  
    + __Netscape Delegated Administrator__ provides real-time user self-service for account management.     
    + __iPlanet Directory Server__

      iPlanet Directory Server delivers a user-management infrastructure for enterprises that manage high volumes of information - for partners, customers, suppliers and others. It integrates with existing systems and acts as a central repository for the consolidation of user profiles.
    + __iPlanet™ Web Proxy Server__ is a powerful system for caching and filtering Web content and boosting network performance.
### [Cobalt Networks](http://www.cobaltnet.com)(~2000)

Popular server appliances extend product family.

1. Products
  + __Qube__ is a server appliance, the heart of an Internet or intranet network.
  + __RaQ__ is a server appliance for Internet Service Providers (ISPs).
    It can host up to 200 individual websites or it can be dedicated to a single medium or large customer.
  + __CacheRaQ__ is a server appliance that solves a common problem: too much network traffic, not enough network.
    It provides local storage, either at the ISP, at the client site, or both, for Web page content.
  + __NASRaQ__ is a massive amount of storage that can be added to a network and shared by everyone connected.

### [HighGround Systems](http://www.highground.com)(1995~2001)

Suite of Web-based management solutions support wide range of storage technologies and applications.

1. The __Storage Resource Manager (SRM)__ product family is an extensible infrastructure for effective enterprise storage resource management.

### [Pirus Networks](http://www.pirus.com)(1999~2002)

a leader in intelligent storage services.

### [Terraspring](http://terraspring.com)(1999~2002)

a pioneer in infrastructure automation software, further enhancing the N1 architecture.

### [Afara Websystems](http://afara.com)(~2002)

a company that develops next-generation SPARC processor-based technology.

### [innotek GmbH](http://innotek.de)(1992~2008)

1. Products
  + __innotek VirtualBox__ is a general purpose virtualization solution for x86 hardware, targeted at desktop, server and embedded use.
    With its large feature set and small footprint, it can easily compete with the established shrink-wrapped virtualization products.
  + __innotek hyperkernel__ combines advanced µ-kernel technology and virtualization into a third-generation hypervisor.
    It currently targets embedded systems and allows for unprecedented system security and reliability.

### [MySQL AB](http://mysql.com)(1995~2008)

1. Products
  + __MySQL Enterprise__ is the most comprehensive offering of MySQL database software, services and support so your business can achieve the highest levels of reliability, security, and uptime.  It includes MySQL Enterprise Server, MySQL Network Monitoring and Advisory Services and MySQL Production Support
  + __MySQL Cluster__ delivers a fault tolerant database clustering architecture for deploying highly available mission-critical database applications.
  + __MySQL Embedded Database__ is the most popular choice for OEMs/ISVs who want to cost-effectively embed or bundle a reliable and high-performance relational database.
  + __MySQL Drivers__: MySQL provides standards-based drivers for JDBC, ODBC, and .Net enabling developers to build database applications in their language of choice. In addition, a native C library allows developers to embed MySQL directly into their applications.
  + __MySQL Tools__: MySQL provides a comprehensive set of open source visual database tools including MySQL Administrator, MySQL Query Browser, and the MySQL Migration Toolkit.
  + __MaxDB__ is the open source database certified for SAP/R3. Formerly known as SAP DB, MaxDB is the result of a strategic alliance between MySQL and SAP to jointly develop and market an open source database for SAP/R3.

## [GoldenGate](http://goldengate.com)(1995~2009)

1. Technology
  + Transactional Data Management (TDM)

    GoldenGate TDM software enables log-based, real-time data movement between heterogeneous databases with transaction integrity.
    The software consists of decoupled process modules that can be configured for high availability/disaster tolerance as well as real-time data integration solutions.
    TDM supports a wide variety of databases and platforms for source and target systems.
  + GoldenGate Director

    GoldenGate Director is a centralized, server-based graphical application that offers an easy way to define, configure, manage, and report on the GoldenGate TDM processes that are deployed across the enterprise.
  + GoldenGate Veridata

    GoldenGate Veridata is a high-speed, low-impact data comparison solution that identifies and reports on data discrepancies between two databases.
    GoldenGate Veridata operates without interrupting those systems or the business applications they support.

2. Solutions
  + High Availability and Disaster Tolerance Solutions
    + Active-Active Systems
    + Zero-Downtime Upgrades
    + Zero-Downtime Migrations
    + Zero-Downtime Maintenance
    + Live Standby
  + Data Integration Solutions
    + Real-Time Data Warehousing
    + Real-Time CDC for ETL Solutions
    + Live Reporting
    + Transactional Data Integration
  + Enterprise Solutions
    + Real-Time Business Intelligence
    + Event-Driven Architecture (EDA)
    + Service-Oriented Architecture (SOA) Support
    + Customer Data Integration (CDI) and Master Data Management (MDM) Support
  + Industry Solutions
    + Banking and Financial Services
    + Healthcare
    + Telecommunications
    + Retail and E-Commerce/E-Business
    + Travel and Hospitality
    + Energy and Utilities
    + Government and Public Sector

## [ClearApp](http://www.clearapp.com)(2002~2008)
    
1. Products
   + QuickVision
2. Solutions
   + Application Service Management (ASN)
   + Application Performance Management (APM)
   + Application Change Management
   + SOA Management

## [AdminServer](http://adminserver.com)(1998~2008)

1. Products: AdminServer System
   1. Rules Engine

      The ability to rapidly support new products without customization of base code is a major differentiator of the AdminServer System. 
      At the heart of the system is an XML-based rules engine which is totally externalized from core application logic. 
      Business rules can be controlled at the company, plan, fund, state, client, policy, coverage, and transaction levels. 
      Business rules contain XML payloads that can include any type of data, even actuarial calculations. 
      Rules are flexible and scalable enough to define any business function. 
      There are no limits to the number of rules that can be implemented for a given system function. 
      Overrides are available for every rule, and may be exercised simply and securely with a complete and thorough audit trail.
   2. Document Server

      Insurance processing is a document intensive exercise. 
      To address the correspondence and reporting needs of our clients, DocumentServer, an integrated component of the AdminServer solution, was developed. 
      Using this facility, documents like policy pages, routine letters and transaction confirmations are fully integrated into the system and treated as natural pieces of activity. 
      DocumentServer also handles all system reporting. 
      Any document can be produced by the DocumentServer in a variety of formats, including Word, Excel, Acrobat, and Rich Text.
      DocumentServer can display, print, fax or email any document to one or many recipients.
   3. Re-Illustration - Proposal Generation

      Administration and proposals are an integrated component of the AdminServer administrative system. 
      The AdminServer illustration and proposal generation component uses the same program objects as the administration system. 
      The resulting functionality is groundbreaking: illustrations and re-illustrations are accurate every time, and require no systems maintenance, as illustration programming is totally eliminated.
2. Industry
   1. Life
      + apse and reinstatement
      + Loans
      + 7702 / corridor testing
      + 1035 exchange processing
      + Minimum premium calculations
      + Reversals
      + Premium suspense
      + Flexible mortality rates
      + Guaranteed values
   2. Annuity
      + Patent pending deposit level valuation
      + Unbundled variable support
      + Unlimited number of funds
      + MVA
      + New money, CD, and guaranteed fixed
      + 403(b) Processing
      + 1035 exchange processing
      + Premium suspense
      + Fail-safe controls
      + Extensive product and plan reporting
   3. Payout and annuitization
      + Disbursement suspense
      + Multiple payees
      + Tax withholding and reporting
      + Single life, joint life, period certain
      + Variable payout reset periods
      + Unbundled variable funds
      + Multiple payment streams
      + Base guarantees
      + Fail safe controls
      + DCA, automatic rebalancing, and payment leveling
   4. Variable / Unit Link
      + Unlimited M&E; tiers
      + Dynamic assignment
      + Failsafe controls
      + Detailed reports
      + Flexable M&E; pricing structure
      + Automatic M&E; band generation and wind-down
   5. Group / worksite
      + Lapse and reinstatement
      + Loans
      + 7702 / corridor testing
      + 1035 exchange processing
      + Minimum premium calculations
      + Reversals
      + Premium suspense
      + Flexible mortality rates
      + Guaranteed values
      + MVA
      + Real-time transactions
      + Complete accounting
      + Valuation
   6. Health
      + Electronic enrollment
      + Underwriting rules
      + Compliance
      + Electronic billing and collection (ACH)
      + Complete accounting
      + Policy and certificate issuance

## [BEA Systems](http://bea.com)(1995~2008)

0. Product Families
  + __BEA AquaLogic®__: Unparalleled business and IT alignment

    The BEA AquaLogic family delivers one of the broadest lines of service-infrastructure products available to ensure successful SOA deployment.
    + BEA AquaLogic User Interaction
    + BEA AquaLogic BPM Suite
    + BEA AquaLogic Integrator
    + BEA AquaLogic Service Bus
    + BEA AquaLogic Data Services Platform
    + BEA AquaLogic Enterprise Security
    + BEA AquaLogic Enterprise Repository
    + BEA AquaLogic Service Registry
    + BEA AquaLogic Commerce Services
    + BEA AquaLogic SOA Management
    + BEA AquaLogic Pages
    + BEA AquaLogic Ensemble
    + BEA AquaLogic Pathways
  + __BEA WebLogic®__: Rock-solid foundation for SOA
    + BEA WebLogic Server

      由收购的Tengah Java Application Server更名而来。
    + BEA WebLogic Portal

      BEA WebLogic Portal simplifies the production and management of custom-fit portals, allowing you to leverage a shared services environment to roll out changes with minimal complexity and effort.
    + BEA WebLogic Integration

      BEA WebLogic Integration delivers the ability to converge two otherwise disparate activities-application integration and application development-into one unified business integration solution.
    + BEA WebLogic® RFID Product Family
    + BEA WebLogic® Real Time

      BEA WebLogic Real Time is a new lightweight, low-latency Java-based server that provides response times in the milliseconds for performance-critical real-time applications.
    + BEA Workshop™

      BEA WebLogic Workshop dramatically reduces the complexity of migrating to SOA, while reducing the overall lifetime costs of your IT infrastructure.
    + BEA JRockit®

      Using BEA JRockit Java Development Kit (JDK), Java developers are able to deploy applications more quickly and efficiently into production, achieving optimal performance through minimal configuration.
+ __BEA Tuxedo®__: Backbone for mission-critical transaction processing systems

  Tuxedo was developed by Bell Labs in 1983 to enable large numbers of users to simultaneously access and manipulate a database on a mainframe computer.
  It was then sold to Novell Inc., with variations developed by other smaller companies.
  In 1996 BEA bought the rights to Tuxedo from Novell, along with much of its independent distribution network.
1. Solutions
  + __Enterprise Social Computing__ for collaboration and user interaction
  + __Business Process Management (BPM)__ for closed-loop business processes
  + __Portals__ for connecting people to your business
  + __Service-Oriented Architecture (SOA)__ to transform business tasks into reusable, loosely-coupled services
  + __Business Integration__ to provide a service-oriented approach to integration for business and IT
  + __SOA Control:  Governance, Management__ and __Security__ for managing change to maintain agility and deliver ROI
  + __Virtualization__ to boost server utilization and optimize computing capacity

### [WebLogic](http://www.weblogic.com)(1995~1998)

1. Products
  + Tengah Java Application Server

    The first commercial Java application server based on Java industry standards.
    Tengah is an extensible platform for assembling, deploying, and managing distributed Java applications.
    With Tengah, Java business components can be interconnected with heterogeneous databases, network information resources, and Java business components.
    Tengah then manages your application components through a graphic Java console to ensure security, scalability, performance, and transaction integrity.
  + Tengah/JDBC

    The Tengah/JDBC allows mobile Java applications to use JavaSoft-standard JDBC to access heterogeneous relational databases from anywhere in the network.
    Tengah/JDBC offers end-to-end security, transactions, and scalability via data and connection caching.
  + jdbcKona

    jdbcKona is a Type 2 (two-tier) JDBC implementation for Oracle, Sybase, or Microsoft SQL Server databases.
    jdbcKona uses vendor client libraries rather than ODBC for higher performance.
    jdbcKona includes DBMS-specific optimizations, security, and transactions.
  + dbKona

    dbKona provides high-level JDBC-compliant connectivity to databases.
    dbKona's flexible objects for database access, like TableDataSet and QueryDataSet, simplify working with data.
  + htmlKona

    htmlKona delivers powerful Java objects for programmatic generation of dynamic HTML documents.

### [Appeal Virtual Machines](http://jrockit.com)(1997~2002)

1. Products: JRockit

   JRockit is a high performance, manageable and configurable Java™ Virtual Machine that is now officially Java™ compatible.

### [Fuego](http://www.fuego.com)(~2006)

1. FuegoBPM

## [Agile](http://www.agile.com)(1995~2007)

1. Agile Product Lifecycle Management (Agile™ PLM)

2. Solutions
   + Aerospace & Defense
   + Automotive Supply Chain
   + Consumer Packaged Goods
     + Food & Beverage
   + Electronics & High Tech
   + Industrial Products
   + Medical Device
   + Pharmaceutical

## [Hyperion](http://hyperion.com)(1998~2007)

0. Products: Hyperion System
  + Financial Applications
    + Financial Management

      Web-based financial software application delivers reporting, financial consolidation and analysis; designed and maintained by the finance team
    + Planning

      Centralized Excel and Web-based planning, budgeting, and forecasting application
    + Capital Expense Planning

      Specialized planning module that automates the planning of capital assets and capital asset related expenses
    + Workforce Planning

      Specialized planning module for headcount, salary, and compensation planning
    + Strategic Finance

      Out-of-the-box financial modeling application for integrated financial statements
    + Performance Scorecard

      Strategy management and performance monitoring application
  + Business Intelligence
    + Interactive Reporting

      User driven, Ad hoc query, data warehouse reporting, and interactive dashboards
    + Production Reporting

      Flexible output, high volume reporting with flexible output options
    + Web Analysis

      Multidimensional reporting and analysis

    + Financial Reporting

      GAAP compliant Management and Statutory financial reporting

    + Essbase® Analytics

      Analytic platform for business modeling and analysis
    + Enterprise Analytics

      High-performance engine for analytic reporting and analysis
  + Data Management Services
    + Data Integration Management

      Hyperion® System™ 9 Data Integration Management™ (Hyperion Data Integration Management) software is a proven data integration platform that enables companies to access, integrate, transform, and move virtually any type of data between Hyperion System 9 and virtually any system, in virtually any frequency, and in virtually any format, thereby eliminating data fragmentation across the enterprise and optimizing Business Performance Management (BPM) and Business Intelligence deployments.
    + Financial Data Quality Management

      Finance organizations need to eliminate the data integrity risks associated with collecting, mapping, verifying and moving critical financial data across the entire organization.
      Hyperion® System™ 9 Financial Data Quality Management™ (Hyperion FDM) software delivers on this requirement, helping finance workers trust their numbers and lower their cost of compliance.
    + Master Data Management

      Hyperion® System™ 9 Master Data Management™ (Hyperion MDM) software is the industry’s first master data management solution built to enable Business Performance Management (BPM).
  + Foundation Services
    + BPM Architect

      Hyperion System 9 BPM Architect, the latest addition to Foundation Services, helps streamline the creation and deployment of financial applications from a central location.
      The visual environment provided by BPM Architect allows for a simple and intuitive user experience in modeling the financial business process, including data, dimensions, and application logic.
      BPM Architect helps users configure these dimensions from an extensible library, link different applications, re-use or move artifacts from one application to another, and graphically manage data flows between applications.
      In this way, BPM Architect provides the industry’s first business process modeling tool for building and maintaining BPM applications.
    + SmartView for Office

      Smart View integrates Hyperion System 9 with Microsoft Office, and allows end users to access BPM content in the Hyperion System 9 modules through Microsoft Excel, Microsoft Word, or Microsoft PowerPoint.
      Smart View supports Smart Tags so that analytical content can be added in context to any Microsoft Office document.
      End users can instantly refresh data within their Office documents and generate polished, professional presentations in minutes.
      Entire briefing books can be refreshed with the latest business content with a single click.
      Smart View also acts as an alternative, Microsoft Office-based interface for data entry for Hyperion System 9 Planning™, Hyperion System 9 Financial Management™, Hyperion System 9 Workforce Planning™ and Hyperion System 9 Capital Expense Planning™.
    + Shared Services

      Shared Services is a single, standardized infrastructure that facilitates the deployment of Hyperion System 9 solutions and simplifies ongoing maintenance.
      It offers a central framework to create and maintain users, manage user security across all Hyperion System 9 modules and facilitates sharing metadata and infrastructure services.
      Shared Services is designed to provide easy integration and interoperability with existing IT assets.
    + Workspace

      Hyperion System 9 Workspace is the central web interface for all users to access Hyperion content.
      The Workspace provides a consistent, interactive and a fully thin-client environment for users to consume all BPM content.
      With its ease-of-use and flexibility, the Workspace provides users with a "windows-on-the-Web" experience.
    + Hyperion License Server

      Hyperion System 9 License Server provides a common license manager across all Hyperion products that helps better enforce the licensing agreements with customers.
      By tracking and enforcing the users and functionalities of Hyperion System 9 modules, licensing services helps customers and Hyperion during compliance reporting and auditing of Hyperion software usage.

1. Industry Solutions
  + Banking
  + Consumer Packaged
  + Goods
  + Energy
  + Federal Government
  + Healthcare
  + Higher Education
  + Insurance
  + Manufacturing
  + Mid-Enterprise
  + Pharmaceutical
  + Retail
  + Telecommunications

## [Tangosol](http://tangosol.com)(2000~2007)

1. Coherence
   
   Tangosol Coherence enables in-memory data management for clustered J2EE applications and application servers. 
   Coherence makes sharing and managing data in a cluster as simple as on a single server. 
   It accomplishes this by coordinating updates to the data using cluster-wide concurrency control, replicating and distributing data modifications across the cluster using the highest performing clustered protocol available, and delivering notifications of data modifications to any servers that request them. 
   Developers can easily take advantage of Coherence features using the standard Java collections API to access and modify data, and use the standard JavaBean event model to receive data change notifications.

## [Sunopsis](http://sunopsis.com)(1998~2006)

1. Products
   + Sunopsis Data Conductor™ –  for ETL projects
   + Sunopsis Active Integration Platform™ –  for Application Integration projects
     + __Active Integration Hub (AIH)__ – which essentially captures the superset of all data and business events contained in all systems linked by the Active Integration Platform.
     + Data Oriented Integration

       A best of breed data integration solution, Data Conductor can be used standalone for data driven integration needs such as ETL. When associated with the other components of the Active Integration Platform, Event Conductor and Service Conductor, Data Conductor provides the data oriented integration mechanisms for the overall integration framework.
       Entirely business-rules-driven, Data Conductor provides the best performance and productivity, for development and execution alike.
     + Event Oriented Integration

       Through its Event Conductor module, the Active Integration Platform automatically implements Changed Data Capture (CDC) in the applications that create events.
     + Service Oriented Integration
       
       Through its Service Conductor module, the Active Integration Platform provides access to data through Web Services that are automatically generated from the defined business rules.

## [Sleepycat](http://www.sleepycat.com)(1996~2006)

1. Products
  + Berkeley DB

    Transactional storage engine for un-typed data in basic key/value data structures.
  + Berkeley DB Java Edition

    Pure Java version of Berkeley DB providing nearly identical features, but with a different architecture optimized for the Java environment.
  + Berkeley DB XML

    Native XML database with XQuery-based access to documents stored in containers and indexed based on their content.
    Berkeley DB XML uses Berkeley DB as its underlying storage engine.

## [360Commerce](http://360commerce.com)(2000~2006)

1. Products
   + 360Store Point-of-Sale

     Designed with the highest degree of flexibility on the market today, this application provides today’s must-have customer service capabilities plus the ability to easily add new functionality as it is needed.
   + 360Store Back Office

     This browser-based application lets managers take care of business from anywhere, including the Point-of-Sale.  
     Coupled with the most comprehensive and easy-to-use reporting system, managers can deliver best-in-class customer service and achieve execution consistency.
   + 360Store Inventory Management

     With real-time, cross-channel look-up capability and the most detailed location information and available-to-sell status, this store inventory solution also provides robust customer service features.
   + 360Enterprise Central Office

     This is the only industry-standard all-in-one solution that reduces the number of third-party applications needed to manage data movement and access real-time information and reports across channels.
   + 360Enterprise Returns Management

     This is the only cross-channel solution on the market today that enables you to reduce fraud without losing your good customers.
   + 360Enterprise Workforce Management

     Your store manager will actually use this solution because it was designed by retail labor scheduling experts to be the easiest for everyone in the organization to understand and operate.

## [Siebel](http://siebel.com)(1993~2006)
0. Products: Siebel CRM
  + Marketing Automation
  + Sales Force Automation
  + Call Center and Service
  + Self Service & eBilling
  + Customer Order Mgmt
  + Partner Relationship Mgmt
  + Business Analytics
1. Industry Solutions
  + Aerospace & Defense
  + Automotive
  + Chemicals
  + Clinical
  + Communications
  + Consumer Goods
  + Energy
  + Finance - Institutional
    + Wealth Management
    + Commercial Banking
    + Institutional Finance
  + Finance - Retail
    + Consumer Banking
    + Branch Banking
    + Consumer Credit
  + Healthcare
  + High Technology
    + Hardware
    + Semiconductors
    + Software
  + Hospitality
  + Industrial Manufacturing
  + Insurance
  + Medical
  + Oil & Gas
  + Pharmaceutical
  + Public Sector
    + Social Services
    + Homeland Security
    + Citizen Response
    + Tax & Revenues
    + Nonprofits
    + Defense Agencies
  + Retail
  + Transportation
  + Travel

## [OctetString](http://octetstring.com)(2000~2005)

1. Products
   + VDE Directory Suite

     The VDE Directory Suite (VDE)is the worlds most powerful directory server. 
     It is a high performance directory server, a proxy to remote LDAP servers, and it provides LDAP access to your RDBMS data.

   + Directory Federator Express

     Directory Federator Express (DFE) is OctetString's tool for federating security information between business partners, and providing directory integration middleware for applications to integrate with varying enterprise directory infrastructures.

   + Directory Server Express

     Directory Server Express (DSE) is OctetString's LDAPv2/v3 directory offering. 
     DSE is designed with a small foot print and can be easily embedded into service infrastructure or software product.

   + VDE DB Proxy

     VDE DB Proxy is OctetString's LDAPv2/v3 gateway to relational database.
     The DB Proxy takes almost any database data design and converts it to standard LDAP form. 
     The DB Proxy is available as part of the VDE Suite, or as a standalone product.

   + JDBC-LDAP Bridge Driver

     The JDBC-LDAP Bridge driver is a JDBC to LDAP bridge that can be used by developers familiar with JDBC to connect to any LDAP directory. 
     The driver is intended to allow developers more comfortable with JDBC to access directory information. 
     This software is now available through OpenLDAP and is available under the OpenLDAP license terms.



## [Innobase Oy](http://www.innodb.com)(1995~2005)

1. InnoDB

   Transactions, row level locking, hot backup, and foreign keys for MySQL - without compromising the speed of MySQL

## [G-Log](http://glog.com)(1999~2005)

1. Products: GC3
   + Order Management
   
     GC3 based on your specific rules in the workflow and automation tools, creates callouts from blanket purchase orders, matches them to production orders and generates transportation orders. 
     GC3 captures key product information, such as SKU, HAZMAT and serial numbers, providing visibility and event management. GC3 supports distributed order management via multiple sources/destinations, involved parties and special instructions.
   + Transportation Planning and Execution
     + __Planning__ – GC3 supports all transportation moves, including inbound and outbound, from simple point-to-point to complex multi-modal, multi-leg and cross-docking operations.
     + __Execution__ – GC3 provides the most complete solution for automated booking/tender loads from a bulk plan and using any combination of easy-to-use web-based, EDI and e-mail interfaces.
     + __Visibility and Event Management__ – With GC3, you can track orders and shipments to the SKU level and find or track inventory anywhere in your logistics network.
   + Contract and Rate Management

     Within GC3 is the industry’s most flexible rating engine to handle multiple-mode, currency and tariff complexities. 
     GC3’s attribute-driven architecture models service provider, mode and geographic nuances. 
     This unique architecture enables hybrid costing capabilities to evaluate freight based on weight, pallet or mileage bands over all modes and geographies.
   + Financials

     GC3 uses actual shipment data captured throughout the logistics process, including accessorial and actual rates vs. planned rates. 
     GC3 provides match and pay as well as auto-pay functionality and supports customer billing and freight invoicing with the industry’s most advanced cost allocation engine.
   + Asset Management

     GC3 can balance demand with available equipment capacity to dynamically configure your network and position assets where needed in your supply chain.
   + Brokerage and Forwarding

     GC3 provides a collaborative environment for brokerage and forwarding agencies to easily create shipments from quotes for services – both domestic and international freight movements – in a single solution.
   + Warehouse Flow Management

     GC3’s warehouse flow management solution allows you to balance the efficiencies of the warehouse picking activities with your transportation constraints, allowing you to effectively consider cost trade-offs between the warehouse operations and the transportation activities.
   + Inventory Management

     Use GC3 to view inventory at rest from multiple distribution centers as well as goods in-transit down to the SKU level. 
     GC3 provides multiple inventory views at multiple levels of aggregation to support VMI and other fulfillment initiatives.
   + Procurement

     GC3 is the only solution you need for your entire procurement process, from RFQ management and rate maintenance to multiple-round combinatorial bid optimization for contracted rates and capacity constraints. 
     GC3 is unique because it is the only solution to automatically pull the data from the active logistics database to build the bid package and seamlessly load the awarded bids back into the execution environment.
   + Business Process Automation (BPA)

     Through GC3’s highly configurable workflow and automation tools, it automates regular tasks such as load building and freight tendering. 
     GC3’s BPA can identify changes in operational assumptions, including shipment arrival times, inventory availability, late orders and other information that may cause delivery or service-level problems. 
     It can then dynamically re-plan or present feasible alternatives to drive effective resolution. 
     Using the automated workflow components, GC3 can also bring in your trading partners and service providers to collaborate on solutions when desired.
2. Solutions
   + Appointment Scheduling
   + Brokerage
   + Claims Management
   + Complete Logistics Platform
   + Distributed Order Management
   + Domestic Inbound
   + Domestic Outbound
   + Freight Audit and Pay
   + Freight Forwarding
   + International Inbound
   + International Outbound
   + Production to Customer Synchronization
   + Rate Management
   + Transportation Procurement
   + Vendor Managed Inventory
   + Warehouse Flow Optimization
3. Industries
   + Consumer Goods
   + Discrete Manufacturing
   + Logistics Service Provider
   + Process Manufacturing
   + Retail

## [TimesTen](https://timesten.com)(1996~2005)

0. Products
  + __TimesTen®/DataServer__ – Real-Time Distributed Database System

    TimesTen/DataServer (DataServer) combines in-memory database technology with N-way data replication, providing real-time data management and data distribution in a high-availability configuration.
    DataServer is most commonly deployed co-resident on the same platform as the application, tightly linked for maximum performance.
  + __TimesTen®/Cache__ – Real-Time Dynamic Data Caching System

    TimesTen/Cache (Cache) includes in-memory database and data exchange technologies.
    Together, they enable applications to combine the real-time performance of TimesTen with the large storage capacity of an RDBMS (currently Oracle).
    Cache automates the loading of specified subsets of the RDBMS into TimesTen, implements TimesTen’s full range of read/write SQL operations against the cached data, and synchronizes changes between the cache and the RDBMS.
  + __TimesTen®/Transact__ – Real-Time Transaction Processing System

    TimesTen/Transact (Transact) combines in-memory database, data replication, event processing, and transaction processing technologies, together with a third-party message bus into a tightly-integrated, guaranteed execution platform.
    Transact provides an infrastructure upon which to build very high-speed transactional applications, such as those used in securities trading, telecom billing, and online commerce.
1. Solutions
  + Financial Services
  + Telecom
  + Networking
  + Real-Time Enterprise

## [PeopleSoft](http://peoplesoft.com)(1987~2005)

0. Product Families
  + PeopleSoft Enterprise

    PeopleSoft® Enterprise is a family of best-in-class applications based on our Pure Internet Architecture® designed for flexible configuration and open, multi-vendor integration.
    It is ideally suited for financial, government, education, healthcare and other services industries.
    It is also ideally suited for large, company-wide functions such as human resources, finance, IT, procurement, marketing, services and sales across all industries.
    + Campus Solutions
      + Academic Advisement
      + Campus Community
      + Campus Self Service
      + Contributor Relations
      + Financial Aid
      + Gradebook
      + Recruiting and Admissions
      + Student Administration
      + Student Financials
      + Student Records
    + Customer Relationship Management
      + Advanced Configurator
      + Bill Presentation and Account Management
      + CRM for Communications
      + CRM for Energy
      + CRM for Financial Services
      + CRM for Government
      + CRM for High Technology
      + CRM for Higher Education
      + CRM for Insurance
      + CRM for Wealth Management
      + CRM Portal Pack
      + CRM Warehouse
      + CTI Integration
      + Customer Behavior Modeling
      + Customer Scorecard
      + FieldService
      + HelpDesk
      + HelpDesk for Human Resources
      + InfoSync
      + Marketing
      + Mobile FieldService
      + Mobile Order Capture
      + Mobile Sales
      + MultiChannel Communications
      + Online Marketing
      + Order Capture
      + Order Capture Self Service
      + Partner Collaborative Commerce
      + Partner Collaborative Sales
      + Partner Lifecycle Marketing
      + Partner Platform
      + Partner Strategic Planning
      + Phone Number Administration
      + Predictive Analytics
      + Real-Time Advisor
      + Sales
      + Sales for BlackBerry
      + Services Management
      + SmartViews
      + Strategic Account Planning
      + Support
      + Support for Customer Self Service
      + Telemarketing
    + Financial Management
      + Activity-Based Management
      + Business Planning and Budgeting
      + Cash Management
      + CFO Portal
      + Deal Management
      + Enterprise Scorecard
      + Enterprise Warehouse
      + EPM Portal Pack
      + ESA Warehouse
      + eSettlements
      + Expenses
      + Financial Management Warehouse
      + Financials Portal Pack
      + Fixed Asset Accounting
      + Funds Transfer Pricing
      + General Ledger
      + Global Consolidations
      + Government Portal
      + Internal Controls Enforcer
      + Investor Portal
      + Payables
      + Project Portfolio Management
      + Purchasing
      + Receivables
      + Risk Management
      + Risk-Weighted Capital
    + Human Capital Management
      + Absence Management
      + Benefits Administration
      + Candidate Gateway
      + Directory Interface
      + eBenefits
      + eCompensation
      + eCompensation Manager Desktop
      + eDevelopment
      + Enterprise Warehouse
      + ePay
      + ePerformance
      + EPM Portal Pack
      + eProfile
      + eProfile Manager Desktop
      + Global Payroll
      + HCM Payroll Process Integration Pack
      + HelpDesk for Human Resources
      + HRMS Portal Pack
      + HRMS Warehouse
      + Human Resources
      + Learning Management
      + Payroll for North America
      + Payroll Interface
      + Payroll Interface for ADP Connection
      + Pension Administration
      + Sales Incentive Management
      + Services Procurement
      + Stock Administration
      + Talent Acquisition Manager
      + Time and Labor
      + Workforce Planning
      + Workforce Rewards
      + Workforce Scorecard
    + Service Automation
      + Billing
      + Contracts
      + ESA Portal Pack
      + ESA Warehouse
      + Expenses
      + General Ledger
      + Global Payroll
      + Grants
      + Human Resources
      + Mobile FieldService
      + Mobile Time and Expense
      + Pay/Bill Management
      + Payroll for North America
      + Program Management
      + Project Costing
      + Project Portfolio Management
      + Proposal Management
      + Receivables
      + Resource Management
      + Services Procurement
      + Staffing Front Office
      + Time and Labor
    + Supplier Relationship Management
      + Catalog Management
      + Collaborative Supply Management
      + eProcurement
      + eSupplier Connection
      + Purchasing
      + Services Procurement
      + Source to Settle Process Integration Pack
      + Strategic Sourcing
      + Supplier Rating System
      + Supply Chain Portal Pack
      + Supply Chain Warehouse
    + Supply Chain Management
      + Activity-Based Management
      + Billing
      + Demand Planning
      + eBill Payment
      + Engineering
      + Enterprise Warehouse
      + EPM Portal Pack
      + Flow Production
      + Inventory
      + Inventory Policy Planning
      + Manufacturing
      + Manufacturing Scorecard
      + Order Management
      + Product Configurator
      + Production and Distribution Planning
      + Production Scheduling
      + Promotions Management
      + Quality
      + Receivables
      + Strategic Network Optimization
      + Supplier Rating System
      + Supply Chain Portal Pack
      + Supply Chain Warehouse
      + Supply Planning Advanced Multisite Planner
      + Supply Planning Multisite Material Planner
      + Tactical Network Optimization
    + Enterprise Tools and Technology
  + JD Edwards EnterpriseOne

    JD Edwards EnterpriseOne is a complete suite of modular, pre-integrated industry-specific business applications designed for rapid deployment and ease of administration on a pure internet architecture.
    It is ideally suited for organizations that manufacture, construct, distribute, service, or manage products or physical assets.
    + Asset Lifecycle Management
    + Customer Relationship Management
    + Financial Management
    + Human Capital Management
    + Manufacturing and Supply Chain Management
    + Project Management
    + Supply Management
    + EnterpriseOne Tools and Technology
  + JD Edwards World

    JD Edwards World is a leading application suite for the IBM iSeries platform.
    The applications are tightly integrated and pre-bundled on a single database, with a Web-enabled architecture.
    + Distribution Management
    + Financial Management
    + Homebuilder Management
    + Human Capital Management
    + Manufacturing Management
    + Self-Service
      + Customer Self-Service
      + Employee Self-Service
      + Supplier Self-Service
1. Industry Solutions
  + Automotive OEM and Heavy Vehicle
  + Automotive Suppliers
  + Banking and Capital Markets
  + Chemicals and Lubricants
  + Communications
  + Construction
  + Consumer Products
  + Energy
  + Federal Government
  + Field Service Organizations
  + Healthcare
  + High-Tech Electronics
  + Higher Education
  + Homebuilder
  + Industrial Manufacturing
  + Insurance
  + K-12 Education
  + Life Sciences
  + Paper and Packaging
  + Professional Services Organization
  + Real Estate
  + Retail
  + Staffing
  + State and Local Government
  + Utilities

### [JD Edwards](http://www.jde.com)(1977~2003)

0. product: J.D. Edwards

  + Enterprise Resource Planning (ERP)

    J.D. Edwards ERP applications are designed to improve a company's internal operations and provide the groundwork for collaborative commerce.
    The ERP applications assist you with managing finance, assets (including inventory, fixed assets and real estate), people, projects, suppliers and the fulfillment and manufacturing processes.
    + Enterprise Foundation: Financial Management, Technical Foundation
    + Assets: Inventory Management, Enterprise Asset Management, Real Estate Management, Advanced Real Estate Forecasting
    + People: Workforce Management, Time and Expense Management
    + Projects: Project Management, Homebuilder Management
    + Suppliers: Procurement, Subcontract Management
    + Fulfillment: Order Management, Manufacturing Management

  + Customer Relationship Management (CRM)

    J.D. Edwards CRM focuses on managing customer relationships well beyond automating sales, marketing and customer service.
    Ultimately, J.D. Edwards CRM will help a company manage its entire customer life cycle, from customer acquisition through fulfillment to post-sales service and support.
    J.D. Edwards CRM includes Sales Force Automation, Marketing Automation, Partner Relationship Management, Service Management, Contact Center, Business Intelligence, Customer Self-Service and Advanced Order Configurator.
    + Advanced Order Configurator
    + Business Intelligence for CRM
    + Contact Center
    + Customer Self-Service
    + Field Service Management
    + Marketing Automation
    + Sales Force Automation
    + Partner Relationship Management

  + Supply Chain Management (SCM)

    J.D. Edwards SCM comprises fully-integrated planning/forecasting and fulfillment applications.
    Working together, J.D. Edwards' supply chain products deliver enhanced productivity and efficiency for product-driven industries.
    In addition, the integration between J.D. Edwards Advanced Planning and fulfillment software—such as Order Management, Procurement, Warehouse Management and Transportation Management—enables instantaneous response to events that impact a company's supply chain.
    + Supply Chain Planning
    + Logistics Management
    + Order Management
    + Manufacturing Management
    + Inventory Management
    + Procurement & Subcontract Management
    + Business Intelligence for SCM

  + Supplier Relationship Management (SRM)

    Buyers have been exchanging information with their suppliers for centuries.
    Unfortunately, phone, fax, and e-mail will not allow your business to react efficiently to poor forecasts, supply chain interruptions, and rapid demand changes.
    Therefore, our Supplier Relationship Management offerings go beyond strategic sourcing and e-procurement to optimize the relationship between enterprises and their suppliers— allowing you to compete more effectively.
    + E-Procurement
    + Procurement Management
    + Supplier Self-Service
    + Demand Consensus
    + Demand Planning
    + Production & Distribution Planning
    + Production Scheduling - Discrete
    + Production Scheduling - Process
    + Strategic Network Optimization
    + Content Management
    + Agreement Management
    + Inventory Management
    + Product Data Management
    + Requirements Planning
    + Warehouse Management
    + Transportation Management
    + Accounts Payable

  + Business Intelligence

    J.D. Edwards Business Intelligence enables companies to extract important information from the data shared among customers, partners and suppliers.
    Its purpose is to help a company monitor its business activities, identify sources of value and measure the level of value delivered.
    A Web-based "dashboard" with easy-to-read gauges shows how the organization performs on an ongoing basis. As users tap different categories of information, new and updated information is automatically pushed to their desktop, phone, fax, or wireless device.
    With J.D. Edwards, users can easily modify their data warehouses and decision-support tools to meet changing information needs.

  + Collaboration and Integration

    J.D. Edwards' collaboration and integration capabilities enable a company to evolve its collaborative commerce strategy over time, as its business needs change.
    Collaboration and Integration includes XPI (eXtended Process Integration), J.D. Edwards' middleware offering, which allows a company to collaborate with multiple suppliers, customers and business partners by linking disparate systems and applications.
    XPI also readies a business for future Web services technology.
    Further, J.D. Edwards offers a library of pre-built XBPs (eXtended Business Processes), inter-application and - enterprise processes that cross proprietary application boundaries to connect business activity over the Internet.
    In addition J.D. Edwards offers Content Manager, a content creation and change management application that helps users to easily create, manage and distribute text-based, multi-language content across an organization's entire global operation.
    + __eXtended Process Integration (XPI™)__—Toolset that helps customers connect applications to applications and companies to companies
    + __eXtended Business Processes (XBP™)__—Application integration that extends XPI to link at the business process level
    + __Portal__—Single point of access to applications and content with roles-based interactions and customizable workspaces
    + __Content Manager__—Collaboration and integration for unstructured data that supports the business process

  + Tools and Technology

    The tools and technology in J.D. Edwards 5 provides the infrastructure for its collaborative enterprise applications, speeding development, deployment and maintenance.
    The toolset gives a business the ability to understand, implement, use and change J.D. Edwards software to keep their technology infrastructure in step with dynamic business requirements.
    + Solution Modeler
    + Autopilot
    + Web-Based Training

1. Solution Families

  + OneWorldŽ

   OneWorld is our  leading-edge, network-centric, object-oriented solution.
   It offers all the integration of WorldSoftware, but can run on a wide variety of hardware systems and configurations.
   With the OneWorld business-transforming technology you are able to implement and change more quickly, without interrupting business processes and information flow.

  + WorldSoftware

    WorldSoftware is our original, time-tested solution for the host-centric IBM AS/400Ž environment.
    It uses a Java™ and HTML-based GUI so that users have the visually familiar interface of a client/server system without compromising data integrity or sacrificing your existing hardware investment.
    With self-service and storefront capabilities, you can extend your WorldSoftware applications to your employees, customers and partners.
    In addition, J.D. Edwards' revolutionary eXtended Process Integration (XPI) technology allows WorldSoftware customers to take advantage of our advanced planning and customer relationship management applications.

2. Industry-Specific Solutions

  + Automotive
  + Chemicals
  + Construction
  + Consumer Products
  + Energy
  + Field Service
  + Financial Services
  + High Tech / Electronics
  + Homebuilders
  + Industrial Manufacturing
  + Life Sciences
  + Mining
  + Professional Services
  + Public Services
  + Paper
  + Real Estate
  + Telecomm
  + Utilities
  + Wholesale Distribution


参考：
1. [Oracle Acquisition](https://en.wikipedia.org/wiki/List_of_acquisitions_by_Oracle)