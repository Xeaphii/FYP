<?php

function latest_posts() {
    global $connection;
    $result = mysql_query("SELECT * FROM `tbl_latest_posts` WHERE 1", $connection);
    while ($row = mysql_fetch_array($result)) {
        echo '<li><a href="';
        echo $row['link'];
        echo '">';
        echo $row['name'];
        echo '</a></li>';
    }
}

function quick_link() {
    global $connection;
    $result = mysql_query("SELECT * FROM `tbl_quick_link` WHERE 1", $connection);
    while ($row = mysql_fetch_array($result)) {
        echo '<div class="bs_box">
                    	<a href="';
        echo $row['link'];
        echo '"><img src="';
        echo $row['image'];
        echo '" alt="';
        echo $row['name'];
        echo '" /></a>
                        <h4><a href="#">';
        echo $row['name'];
        echo '</a></h4>
                        <p class="price">';
        echo $row['desc'];
        echo '</p>
                        <div class="cleaner"></div>
                    </div>';
    }
}

function posts() {
    global $connection;
    $result = mysql_query("SELECT * FROM `tbl_quick_link` WHERE 1", $connection);

    while ($row = mysql_fetch_array($result)) {
        echo '<div class="product_box">
                <h3>Name of Post</h3>
                    <a href="productdetail.html" ><img  style="height:200px; width:200px" src="images/product/01.gif" alt="Shoes 1" /></a>
                    <p>Desc of post</p>
                </div> ';
    }
}

function load_images_to_slider() {
    if (isset($_GET['page_name'])) {
        $page_name = $_GET['page_name'];
    } else {
        echo '<META HTTP-EQUIV="Refresh" Content="0; URL=errorpage.php">';    
    exit;
    }
    global $connection;
    $result = mysql_query("SELECT * FROM `tbl_nav_sub_items` where `item_name` = '".$page_name."';", $connection);

    while ($row = mysql_fetch_array($result)) {
        echo '<img src="';
        echo $row["full_size_image"];
        echo '" alt="';
        echo $row["name"];
        echo '" />';
    }
}

?>

