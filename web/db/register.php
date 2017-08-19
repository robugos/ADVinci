<?php
 
require_once dirname(__FILE__) . '/db_functions.php';
$db = new DB_Functions();
 
// json response array
$response = array("error" => FALSE);
 
if (isset($_POST['nome']) && isset($_POST['sobrenome']) && isset($_POST['email']) && isset($_POST['senha'])) {
 
    // receiving the post params
    $nome = $_POST['nome'];
    $sobrenome = $_POST['sobrenome'];
    $email = $_POST['email'];
    $senha = $_POST['senha'];
 
    // check if usuario is already existed with the same email
    if ($db->isUserExisted($email)) {
        // usuario already existed
        $response["error"] = TRUE;
        $response["error_msg"] = "Usuario existente com email " . $email;
        echo json_encode($response);
    } else {
        // create a new usuario
        $usuario = $db->storeUser(utf8_decode($nome),utf8_decode($sobrenome), utf8_decode($email), $senha);
        if ($usuario) {
            // usuario stored successfully
            $response["error"] = FALSE;
            $response["uid"] = $usuario["unique_id"];
            $response["usuario"]["nome"] = utf8_decode($usuario["nome"]);
            $response["usuario"]["sobrenome"] =  utf8_decode($usuario["sobrenome"]);
            $response["usuario"]["email"] =  utf8_decode($usuario["email"]);
            $response["usuario"]["criado_em"] =  utf8_decode($usuario["criado_em"]);
            $response["usuario"]["atualizado_em"] =  utf8_decode($usuario["atualizado_em"]);
            echo json_encode($response);
        } else {
            // usuario failed to store
            $response["error"] = TRUE;
            $response["error_msg"] = "Erro desconhecido no registro.";
            echo json_encode($response);
        }
    }
} else {
    $response["error"] = TRUE;
    $response["error_msg"] = "Campos obrigatorios faltando.";
    echo json_encode($response);
}
?>