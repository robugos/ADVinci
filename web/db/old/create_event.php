<?php

//array para resposta JSON
$response = array();

$jsonInput = $_POST['json'];
$evento = json_decode($jsonInput, true);
if ( isset($evento['nomeEvento']) ) {
    $nomeEvento = $ocorrencia['nomeEvento'];
    $localEvento = $ocorrencia['localEvento'];
    $descricaoEvento = $ocorrencia['descricaoEvento'];
    $dataEvento = $ocorrencia['dataEvento'];

    // include de conexão com o DB
    require_once dirname(__FILE__) . '/db_connect.php';

    // conecta com o DB
    $db = new DB_CONNECT();

    // insert mysql
    $result = mysql_query(
        "INSERT INTO EVENTO(nome, local, descricao, data) VALUES('$nomeEvento', '$localEvento', '$descricaoEvento', '$dataEvento')"
        );

    // checa se foi inserido ou não
    if ($result) {
        // successfully inserted into database
        $response["success"] = 1;
        $response["message"] = "Evento criado.";

        // echoing JSON response
        echo json_encode($response);
    } else {
        // failed to insert row
        $response["success"] = 0;
        $response["message"] = "Opa! Ocorreu um erro.";
        $response["nomeEvento"] = $nomeEvento;
        $response["localEvento"] = $localEventoice;
        $response["descricaoEvento"] = $descricaoEvento;
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