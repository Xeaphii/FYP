<?php
$Dir = md5($_GET['dir']);
if (!file_exists('user/'.$Dir)) {
    mkdir('user/'.$Dir, 0777, true);
}
?>