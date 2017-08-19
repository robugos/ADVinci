<?php
require_once dirname(__FILE__) . '/db_functions.php';
$db = new DB_Functions();
 
$response = array("success"=>1, "evento"=>array());
$id = (int)$_GET["id"];
//echo $id;
$evento = $db->getEventById($id);

if ($evento != false) {
    $lista = array();
    $i=0;
    foreach($evento as $eve){
        
        $lista[$i]["id"] = $eve["id"];
        $lista[$i]["nome"] = $eve["name"];
        $lista[$i]["local"] = $eve["place"];
        $lista[$i]["data"] = $eve["start_time"];
        $lista[$i]["descricao"] = $eve["description"];
        $lista[$i]["urlimg"] = $eve["cover"];
        $lista[$i]["adimg"] = $eve["name"];
        $lista[$i]["nota"] = $db->getAVGNote($eve["id"]);
        
        $i=$i+1;
    }
    $response["evento"] = $lista;
    //print_r($response);
    $response["success"] = 1;
    echo json_encode($response);
} else {
    $response["success"] = 0;
    $response["message"] = "Nenhum evento encontrado.";
    echo json_encode($response);
}
?>