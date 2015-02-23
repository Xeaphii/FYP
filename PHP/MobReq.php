<?php
if (isset($_POST['id'])) {
    $id = $_POST['id'];
}
$servername = "fdb12.biz.nf";
$username = "1756162_deets";
$password = "AKZK1994";
$dbname = "1756162_deets";

// Create connection
$conn = new mysqli($servername, $username, $password, $dbname);
// Check connection
if ($conn->connect_error) {
     die("Connection failed: " . $conn->connect_error);
} 

$sql = "UPDATE `tbl_switch` SET `value`=".$id;
$result = $conn->query($sql);

$conn->close();
?>  