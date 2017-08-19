<?php
error_reporting (E_ALL & ~ E_NOTICE & ~ E_DEPRECATED);
/*
 * Following code will list all the ocorrencia
 */

// array for JSON response
$response = array();


// include db connect class
require_once dirname(__FILE__) . '/db_connect.php';

// connecting to db
$db = new DB_CONNECT();

// get all ocorrencia from ocorrencia table
mysql_query('SET CHARACTER SET utf8');
$result = mysql_query("SELECT * FROM EVENTO") or die(mysql_error());

// check for empty result
if (mysql_num_rows($result) > 0) {
    // looping through all results
    // node
    $response["eventos"] = array();
    
    while ($row = mysql_fetch_array($result)) {
        // temp user array
        $eventos = array();
        $eventos["id"] = $row["id"];
        $eventos["nome"] = $row["nome"];
        $eventos["local"] = $row["local"];
        $eventos["data"] = $row["data"];
        $eventos["descricao"] = $row["descricao"];
        $eventos["urlimg"] = $row["urlimg"];
        $eventos["adimg"] = $row["adimg"];



        // push single ocorrencia into final response array
        array_push($response["eventos"], $eventos);
    }
    // success
    $response["success"] = 1;

    // echoing JSON response

    echo json_encode($response);
} else {
    // no ocorrencia found
    $response["success"] = 0;
    $response["message"] = "Nenhum evento encontrado.";

    // echo no users JSON
    echo json_encode($response);
}

function fixBadUnicodeForJson($str) {
    $str = preg_replace("/\\\\u00([0-9a-f]{2})\\\\u00([0-9a-f]{2})\\\\u00([0-9a-f]{2})\\\\u00([0-9a-f]{2})/e", 'chr(hexdec("$1")).chr(hexdec("$2")).chr(hexdec("$3")).chr(hexdec("$4"))', $str);
    $str = preg_replace("/\\\\u00([0-9a-f]{2})\\\\u00([0-9a-f]{2})\\\\u00([0-9a-f]{2})/e", 'chr(hexdec("$1")).chr(hexdec("$2")).chr(hexdec("$3"))', $str);
    $str = preg_replace("/\\\\u00([0-9a-f]{2})\\\\u00([0-9a-f]{2})/e", 'chr(hexdec("$1")).chr(hexdec("$2"))', $str);
    $str = preg_replace("/\\\\u00([0-9a-f]{2})/e", 'chr(hexdec("$1"))', $str);
    return $str;
}   

?>