<?php
 
require_once dirname(__FILE__) . '/db_functions.php';
$db = new DB_Functions();
 
// json response array
$response = array("error" => FALSE);
 
if (isset($_POST['uid']) && isset($_POST['interesses'])) {
 
    // receiving the post params
    $uid = $_POST['uid'];
    $interesses = $_POST['interesses'];
    //echo $uid;
    //echo $interesses;

    $update = $db->updateInterest($uid, $interesses);
    if ($update) {
        $response["error"] = FALSE;
        echo json_encode($response);
    } else {
        $response["error"] = TRUE;
        $response["error_msg"] = "Erro desconhecido no registro.";
        echo json_encode($response);
    }
} else {
    $response["error"] = TRUE;
    $response["error_msg"] = "Campos obrigatorios faltando.";
    echo json_encode($response);
}
?>