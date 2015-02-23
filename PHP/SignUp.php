<?php
include 'deets.php';


if (isset($_GET['username'])){
        $UserName = $_GET["username"];
}
if (isset($_GET['password'])){
        $Password = md5($_GET["password"]);
}
if (isset($_GET['email'])){
        $Email = $_GET["email"];
}

$conn = new mysqli($servername, $username, $password, $dbname);
// Check connection
if ($conn->connect_error) {
    die("Connection failed: " . $conn->connect_error);
} 

$sql = "INSERT INTO `USER`( `user_name`, `password`, `version`, `email`) VALUES ('". $UserName."','".$Password."',1,'".$Email."')";

if ($conn->query($sql) === TRUE) {
    echo "Success";
} else {
    echo "Error";
}

$conn->close();
?>