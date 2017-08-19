<?php
require_once dirname(__FILE__) . '/db_functions.php';
$db = new DB_Functions();
$response = array("success"=>1, "eventos"=>array());
$eventos = $db->getEventos();
$uid = $_GET["uid"];
$userinteresses = $db->getUserInteresses($uid);

if ($eventos != false) {
    $lista = array();
    $i=0;
    foreach($eventos as $evento){
        
        $lista[$i]["id"] = $evento["id"];
        $lista[$i]["nome"] = $evento["name"];
        $lista[$i]["local"] = $evento["place"];
        $lista[$i]["data"] = $evento["start_time"];
        $lista[$i]["descricao"] = $evento["description"];
        $lista[$i]["urlimg"] = $evento["cover"];
        $lista[$i]["adimg"] = $evento["name"];
        $lista[$i]["categoria"] = $evento["category"];
        $lista[$i]["nota"] = $db->getAVGNote($evento["id"]);
              
        $i=$i+1;
    }
    $response["eventos"] = $lista;
    //print_r($response);
    $response["userinteresses"] = $userinteresses["interesses"];
    $response["success"] = 1;
    echo json_encode($response);
} else {
    $response["success"] = 0;
    $response["message"] = "Nenhum evento encontrado.";
    echo json_encode($response);
}
?>