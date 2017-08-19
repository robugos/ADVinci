<?php
require_once dirname(__FILE__) . '/db_functions.php';
$db = new DB_Functions();
 
// json response array
$response = array("error" => FALSE);
 
if (isset($_POST['email']) && isset($_POST['senha'])) {
 
    // receiving the post params
    $email = $_POST['email'];
    $senha = $_POST['senha'];
 
    // get the usuario by email and senha
    $usuario = $db->getUserByEmailAndPassword($email, $senha);
 
    if ($usuario != false) {
        // use is found
        $response["error"] = FALSE;
        $response["uid"] = $usuario["unique_id"];
        $response["usuario"]["nome"] = utf8_encode($usuario["nome"]);
        $response["usuario"]["sobrenome"] = utf8_encode($usuario["sobrenome"]);
        $response["usuario"]["email"] = utf8_encode($usuario["email"]);
        $response["usuario"]["criado_em"] = $usuario["criado_em"];
        $response["usuario"]["atualizado_em"] = $usuario["atualizado_em"];
        echo json_encode($response);
    } else {
        // usuario is not found with the credentials
        $response["error"] = TRUE;
        $response["error_msg"] = "Dados de login invalidos. Tente novamente.";
        echo json_encode($response);
    }
} else {
    // required post params is missing
    $response["error"] = TRUE;
    $response["error_msg"] = "Campos obrigatorios faltando.";
    echo json_encode($response);
}
?>