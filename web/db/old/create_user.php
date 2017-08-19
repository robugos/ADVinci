<?php

//array para resposta JSON
echo (file_get_contents('php://input'));
$jsonString = file_get_contents('php://input');
$json = json_decode($jsonString, true);
//$json = file_get_contents('php://input');
$usuario= json_decode($json, true);
if ( isset($usuario['email']) ) {
    $emailUsuario = $usuario['email'];
    $senhaUsuario = $usuario['senha'];
//    $hashsenha = password_hash($senhaUsuario, PASSWORD_DEFAULT);
    $nomeUsuario = $usuario['nome'];
    $sobrenomeUsuario = $usuario['sobrenome'];
    $facebookidUsuario = $usuario['facebookid'];
    $googleidUsuario = $usuario['googleid'];

    // include de conexão com o DB
    require_once dirname(__FILE__) . '/db_connect.php';

    // conecta com o DB
    $db = new DB_CONNECT();

    // insert mysql
    $result = mysql_query(
        "INSERT INTO USUARIO(email, senha, nome, sobrenome, facebookid, googleid) VALUES('$emailUsuario', '$senhaUsuario', '$nomeUsuario', '$sobrenomeUsuario', '$facebookidUsuario', $googleidUsuario')"
        );

    // checa se foi inserido ou nao
    if ($result) {
        // successfully inserted into database
        $response["success"] = 1;
        $response["message"] = "Usuario criado.";

        // echoing JSON response
        echo json_encode($response);
    } else {
        // failed to insert row
        $response["success"] = 0;
        $response["message"] = "Opa! Ocorreu um erro.";
        $response["emailUsuario"] = $emailUsuario;
        //$response["senhaUsuario"] = $senhaUsuarioice;
        $response["nomeUsuario"] = $nomeUsuario;
        $response["query"] = $result;
        
        // echoing JSON response
        echo json_encode($response);
    }

} else {

    // required field is missing
    $response["success"] = 0;
    $response["message"] = $jsonInput;

    // echoing JSON response
    echo json_encode($response);

}
?>