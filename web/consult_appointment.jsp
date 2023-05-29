<%@page import="java.sql.ResultSet"%>
<!DOCTYPE html>
<html lang="en">

<head>

    <meta charset="utf-8">
    <meta http-equiv="X-UA-Compatible" content="IE=edge">
    <meta name="viewport" content="width=device-width, initial-scale=1, shrink-to-fit=no">
    <meta name="description" content="">
    <meta name="author" content="">

    <title>View Appointment</title>

    <!-- Custom fonts for this template -->
    <link href="vendor/fontawesome-free/css/all.min.css" rel="stylesheet" type="text/css">
    <link
        href="https://fonts.googleapis.com/css?family=Nunito:200,200i,300,300i,400,400i,600,600i,700,700i,800,800i,900,900i"
        rel="stylesheet">

    
    
    <!-- Custom styles for this template-->
    <link rel="stylesheet" href="assets/css/sb-admin-2.min.css" type="text/css">
    <link rel="stylesheet" href="assets/css/sb-admin-2.css" type="text/css"/>

</head>

<body id="page-top">
    <jsp:useBean id="db" scope="page" class="com.unikl.umams.web.DBController" />

    <!-- Page Wrapper -->
    <div id="wrapper">

        <!-- Sidebar -->
        <ul class="navbar-nav bg-gradient-primary sidebar sidebar-dark accordion" id="accordionSidebar">

            <!-- Sidebar - Brand -->
            <a class="sidebar-brand d-flex align-items-center justify-content-center" href="index.html">
                <div class="sidebar-brand-icon rotate-n-15">
                    <i class="fas fa-laugh-wink"></i>
                </div>
                <div class="sidebar-brand-text mx-3">UMAMS</div>
            </a>

            <!-- Divider -->
            <hr class="sidebar-divider my-0">

            <!-- Nav Item - Dashboard -->
            <li class="nav-item active">
                <a class="nav-link" href="doctor_dashboard.jsp">
                    <i class="fas fa-fw fa-tachometer-alt"></i>
                    <span>Manage Appointments</span></a>
            </li>
            <!-- Divider -->
            
        </ul>
        <!-- End of Sidebar -->

        <!-- Content Wrapper -->
        <div id="content-wrapper" class="d-flex flex-column">

            <!-- Main Content -->
            <div id="content">

                <!-- Topbar -->
                <nav class="navbar navbar-expand navbar-light bg-white topbar mb-4 static-top shadow">
                    <ul class="navbar-nav ml-auto">
                        <div class="topbar-divider d-none d-sm-block"></div>
                        <!-- Nav Item - User Information -->
                        <li class="nav-item dropdown no-arrow">
                            <a class="nav-link dropdown-toggle" href="#" id="userDropdown" role="button"
                                data-toggle="dropdown" aria-haspopup="true" aria-expanded="false">
                                <span class="mr-2 d-none d-lg-inline text-gray-600 small">${email}</span>
                                <img class="img-profile rounded-circle"
                                    src="assets/img/undraw_profile.svg">
                            </a>
                            <!-- Dropdown - User Information -->
                            <div class="dropdown-menu dropdown-menu-right shadow animated--grow-in"
                                aria-labelledby="userDropdown">
                                <a class="dropdown-item" href="#" data-toggle="modal" data-target="#logoutModal">
                                    <i class="fas fa-sign-out-alt fa-sm fa-fw mr-2 text-gray-400"></i>
                                    Logout
                                </a>
                            </div>
                        </li>
                    </ul>
                    

                </nav>
                <!-- End of Topbar -->

                <div class="col-lg-12">

                    

                    <!-- Basic Card Example -->
                    <div class="card shadow mb-4">
                        <div class="card-header py-3">
                            <h6 class="m-0 font-weight-bold ">Appointment ID: ${appointmentID}</h6>
                        </div>
                        <div class="card-body">
                            <div class="row">
                                <div class="col-md-3">Appointment Date & Time: </div><div class="col-md-6">${appointmentDate}, ${appointmentTime}</div>
                            </div>
                            <div class="row">
                                <div class="col-md-3">Appointment Status: </div><div class="col-md-6">${status}</div>
                            </div>
                            
                            <hr>
                            <div class="row">
                                <div class="col-md-3">Symptoms: </div><div class="col-md-6">${symptoms}</div>
                            </div>
                            <div class="row">
                                <div class="col-md-3">Additional Description: </div><div class="col-md-6">${additionalDescription}</div>
                            </div>
                            <hr>
                            <form action="DoctorServlet" method="post" >
                                <div class="row">
                                    <div class="col-md-3">Weight (KG): </div>
                                    <div class="col-md-2"><input type="number" step="0.1" min="0.0" name="weight" class="form-control" ></div>
                                    <div class="col-md-2">Temperature (�C): </div>
                                    <div class="col-md-2"><input type="number" step="0.1" min="0.0" name="temperature" class="form-control" ></div>
                                </div>
                                <br>
                                <div class="row">
                                    <div class="col-md-3">Blood Pressure, Systolic (mmHg): </div>
                                    <div class="col-md-2"><input type="number" step="0.1" min="0.0" name="systolicBP" class="form-control" ></div>
                                    <div class="col-md-2">Blood Pressure, Diastolic (mmHg):</div>
                                    <div class="col-md-2"><input type="number" step="0.1" min="0.0" name="diastolicBP" class="form-control" ></div>
                                </div>
                                <br>
                                <div class="row">
                                    <div class="col-md-3">Oxygen Level (%):</div>
                                    <div class="col-md-2"><input type="number" step="0.1" min="0.0" name="oxygenLevel" class="form-control" ></div>
                                </div>
                                <hr>
                                <div class="row">
                                    <div class="col-md-3">Diagnosis: </div><div class="col-md-6"><input name="diagnosis" type="text" class="form-control" ></div>
                                </div>
                                <br>
                                <div class="row">
                                    <div class="col-md-3">Additional Notes: </div><div class="col-md-6"><textarea type="text" name="additionalNotes" class="form-control" placeholder="Write notes regarding the patient here"></textarea></div>
                                </div>
                                <br>
                                <div class="row">
                                    <div class="col-md-3">Prescription: </div><div class="col-md-6"><textarea type="text" name="prescription" class="form-control" placeholder="Write patient prescription here"></textarea></div>
                                </div>

                                <br>
                                <input type="hidden" id="custId" name="appointmentID" value=${appointmentID}>
                                <div class="row">
                                    <div class="col-md-6">
                                        <button type="submit" class="btn btn-primary mr-1" name="submit" value="complete">Complete Appointment</button>
                                        <button type="submit" class="btn btn-light mr-1" name="submit" value="cancel">Cancel Appointment</button>
                                    </div>
                                </div>
                                <hr>
                                <div class="row">
                                    <div class="col-md-3"><a class="mr-1" href="doctor_dashboard.jsp">Return to homepage</a></div>
                                </div>
                            </form>
                        </div>
                        
                    </div>
                </div>  
            </div>
            <!-- End of Main Content -->

            <!-- Footer -->
            <footer class="sticky-footer bg-white">
                <div class="container my-auto">
                    <div class="copyright text-center my-auto">
                        <span>Copyright &copy; UMMC Medical Appointment Management System 2023</span>
                    </div>
                </div>
            </footer>
            <!-- End of Footer -->

        </div>
        <!-- End of Content Wrapper -->

    </div>
    <!-- End of Page Wrapper -->

    <!-- Scroll to Top Button-->
    <a class="scroll-to-top rounded" href="#page-top">
        <i class="fas fa-angle-up"></i>
    </a>

    <!-- Logout Modal-->
    <div class="modal fade" id="logoutModal" tabindex="-1" role="dialog" aria-labelledby="exampleModalLabel"
        aria-hidden="true">
        <div class="modal-dialog" role="document">
            <div class="modal-content">
                <div class="modal-header">
                    <h5 class="modal-title" id="exampleModalLabel">Ready to Leave?</h5>
                    <button class="close" type="button" data-dismiss="modal" aria-label="Close">
                        <span aria-hidden="true">�</span>
                    </button>
                </div>
                <div class="modal-body">Select "Logout" below if you are ready to end your current session.</div>
                <div class="modal-footer">
                    <form action="AdminServlet" method="POST">
                        <button class="btn btn-primary" type="submit" name="submit" value="adminLogout">Logout</button>
                    </form>
                    <button class="btn btn-secondary" type="button" data-dismiss="modal">Cancel</button>
                </div>
            </div>
        </div>
    </div>

    <!-- Bootstrap core JavaScript-->
    <script src="assets/vendor/jquery.min.js"></script>
    <script src="assets/vendor/bootstrap.bundle.min.js"></script>

    <!-- Core plugin JavaScript-->
    <script src="assets/vendor/jquery.easing.min.js"></script>

    <!-- Custom scripts for all pages-->
    <script src="assets/js/sb-admin-2.min.js"></script>

    <!-- Page level plugins -->
    
    <script src="assets/vendor/dataTables.bootstrap4.min.js"></script>

    <!-- Page level custom scripts -->
    <script src="assets/js/demo/datatables-demo.js"></script>

</body>

</html>