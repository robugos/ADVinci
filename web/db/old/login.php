<?php

$json = file_get_contents('php://input');
$obj = json_decode($json);
$email = $obj->{'email'};
$senha = $obj->{'senha'};

error_reporting (E_ALL & ~ E_NOTICE & ~ E_DEPRECATED);
// array for JSON response
$response = array();


// include db connect class
require_once dirname(__FILE__) . '/db_connect.php';

// connecting to db
$db = new DB_CONNECT();

$sql = $bdd->prepare(
'SELECT * FROM USUARIO WHERE (email = $email)');
if (!empty($email)) {
    $sql->execute(array(
        'email' => $email,
        'nome' => $nome,
        'sobrenome' => $sobrenome,
        'facebookid' => $facebookid,
        'googleid' => $googleid));
}

// check for post data
if (isset($_POST['email'], $_POST['senha'])) {
    $email = $_POST['email'];
    $senha = $_POST['senha'];

    // get a usuario from usuarios table
    $resultado = mysql_query("SELECT * FROM USUARIO WHERE email = $email");

    if (!empty($resultado)) {
        // check for empty resultado
        if (mysql_num_rows($resultado) > 0) {

            $resultado = mysql_fetch_array($resultado);

            $hash = $resultado["senha"];
            if (password_verify($senha, $hash)){
                $usuario = array();
                $usuario["nome"] = $resultado["nome"];
                $usuario["sobrenome"] = $resultado["sobrenome"];
                $usuario["email"] = $resultado["email"];
                // success
                $response["success"] = 1;

                // user node
                $response["usuario"] = array();

                array_push($response["usuario"], $usuario);

                // echoing JSON response
                echo json_encode($response);
            } else {
                $response["success"] = 0;
                $response["message"] = "Usuario ou senha invalidos";
            }
        } else {
            // no usuario found
            $response["success"] = 0;
            $response["message"] = "Usuario ou senha invalidos";

            // echo no users JSON
            echo json_encode($response);
        }
    } else {
        // no usuario found
        $response["success"] = 0;
        $response["message"] = "Usuario ou senha invalidos";

        // echo no users JSON
        echo json_encode($response);
    }
} else {
    // required field is missing
    $response["success"] = 0;
    $response["message"] = "Faltando campo obrigatorio";

    // echoing JSON response
    echo json_encode($response);
}
?>