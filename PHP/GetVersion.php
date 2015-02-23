<?php
include 'deets.php';


if (isset($_GET['username'])){
        $UserName = $_GET["username"];
}


$conn = new mysqli($servername, $username, $password, $dbname);
// Check connection
if ($conn->connect_error) {
    die("Connection failed: " . $conn->connect_error);
} 

$sql = "SELECT DISTINCT USER.version FROM tbl_switch, USER WHERE USER.index = tbl_switch.user_index and USER.user_name='".$UserName."'";
$result = $conn->query($sql);

if ($result->num_rows > 0) {
    // output data of each row
    while($row = $result->fetch_assoc()) {
        echo "Version:". $row["version"];
    }
} else {
    echo "0";
}

$conn->close();
?>