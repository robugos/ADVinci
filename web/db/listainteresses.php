<?php
require_once dirname(__FILE__) . '/db_functions.php';
$db = new DB_Functions();
 
$response = array("success"=>1, "interesses"=>array());
$interesses = $db->getInteresses();
$uid = $_GET["uid"];
$userinteresses = $db->getUserInteresses($uid);

if ($interesses != false) {
    $lista = array();
    $i=0;
    foreach($interesses as $interesse){
       
        $lista[$i]["id"] = $interesse["id"];
        $lista[$i]["nome"] = $interesse["title"];
        $lista[$i]["categoria"] = $interesse["category"];
        
        $i=$i+1;
    }
    $response["interesses"] = $lista;
    //print_r($response);
    $response["userinteresses"] = $userinteresses["interesses"];
    $response["success"] = 1;
    echo json_encode($response);
} else {
    $response["success"] = 0;
    $response["message"] = "Nenhum interesse encontrado.";
    echo json_encode($response);
}
?>