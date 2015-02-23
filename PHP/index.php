<?php
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


if (isset($_GET['id'])){
        $id = $_GET["id"];
}else{
        $id = 1;
}
if (isset($_GET['pf'])&&isset($_GET['label'])&&isset($_GET['current'])&&isset($_GET['voltage'])){
        $label= $_GET["label"];
        $current = $_GET["current"];
        $voltage = $_GET["voltage"];
        $pf = $_GET["pf"];
        
        $querry = "INSERT INTO `tb-data`( `label`, `current`, `voltage`, `pf`) VALUES (".$label.",".$current.",".$voltage.",".$pf.");";
        
        if (mysqli_query($conn, $querry)) {
            
        } else {
            echo "Error: " . $querry . "<br>" . mysqli_error($conn);
        }
}


$sql = "SELECT * FROM `tbl_switch` WHERE  `key` = ".htmlspecialchars($id).";";
$result = $conn->query($sql);

if ($result->num_rows > 0) {
     // output data of each row
     while($row = $result->fetch_assoc()) {
         echo $row["value"];
     }
} else {
     echo "0 results";
}
$conn->close();
?>  