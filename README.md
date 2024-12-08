# Metro Billing System

Welcome to the **Metro Billing System** GitHub repository! This project is a comprehensive Java-based application designed to manage billing, inventory, and administrative tasks for businesses. The system is built on the **MVC (Model-View-Controller)** design pattern to ensure modularity, scalability, and ease of maintenance.

---

## **Features**

- **Role-Based Access**: Separate interfaces for Super Admin, Branch Manager, Cashier, and Data Entry Operator, each with specific functionalities.
- **Real-Time Barcode Scanning**: Integrated **OpenCV** for real-time barcode and QR code scanning using the system's camera.
- **Comprehensive Reporting**: Generate visual reports, including pie and bar charts, for sales distribution and performance analysis.
- **Offline Operations**: Log offline operations for synchronization when connectivity is restored.
- **PDF Generation**: Create invoices and reports using the `itextpdf` library.
- **Database Integration**: Seamless MySQL integration for managing data, with secure and optimized queries.

### **Adding JAR Files**

To run this project, you need to add the required JAR files. These libraries enhance the system by providing features like database connectivity, chart generation, and real-time scanning.

#### **Required JARs**

- `core-3.5.3`
- `itextpdf-5.5.13.4`
- `jfreechart-1.5.5`
- `junit-4.13.2`
- `junit-jupiter-api-5.11.3`
- `junit-jupiter-engine-5.11.3`
- `mysql-connector-j-9.1.0`
- `opencv-4100`

  #### **Steps to Add JARs**

1. Download the JAR files listed above (or locate them in the `lib` directory if provided).
2. Open your project in an IDE like **IntelliJ IDEA** or **Eclipse**.
3. Add the JARs to your project:
   - **In IntelliJ IDEA**:
     - Go to `File > Project Structure > Libraries`.
     - Click `+` to add the JAR files.
   - **In Eclipse**:
     - Right-click on the project.
     - Go to `Build Path > Configure Build Path > Libraries`.
     - Click `Add External JARs` and select the JAR files.
4. Save the configuration and rebuild the project.

## **Process Flow**

### **1. Splash Screen**
- The application starts with a visually appealing **splash screen** that displays the system loading progress and branding.

### **2. Role Selection**
- Users choose their role (Super Admin, Branch Manager, Cashier, or Data Entry Operator) from the **Main Login Page**.

### **3. Login**
- Role-based login validates user credentials and redirects to the respective dashboard.

### **4. Core Functionalities**
   - **Super Admin**:
     - Add branches and branch managers.
     - View system-wide reports.
   - **Branch Manager**:
     - Manage employees (cashiers and data entry operators).
     - Monitor branch-specific operations.
   - **Cashier**:
     - Handle product sales using the **real-time barcode scanner**.
     - Generate bills and manage product stock.
   - **Data Entry Operator**:
     - Manage product inventory and vendor details.

### **5. Reporting and Analysis**
- Users can view reports with interactive pie and bar charts, filtered by timeframes (weekly, monthly, annually).

---

## **Special Features**

### **Real-Time Camera Scanner**
Our system integrates **OpenCV** to provide a camera-based QR and barcode scanner. This feature eliminates the need for external barcode scanning devices, offering a seamless and cost-effective solution.

- Detects QR codes or barcodes in real time using the system's camera.
- Provides audio feedback (via `beep_sound.wav`) on successful scans.
- Ensures accurate and efficient product entry during sales.

---

## **Developers**

- **Saim Imran**: [cym786@gmail.com](mailto:cym786@gmail.com)  
- **Talha Tofeeq**: [talhatofeeq2003@gmail.com](mailto:talhatofeeq2003@gmail.com)  



