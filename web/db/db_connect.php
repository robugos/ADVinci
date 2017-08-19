<?php

/*if (extension_loaded('mysqli')) { 
    echo 'extension mysqli is loaded<br>'; //works 
}

$mysqlnd = function_exists('mysqli_stmt_get_result');

if ($mysqlnd) {
    echo 'mysqlnd enabled!';
}
else
   var_dump("nope");*/
date_default_timezone_set('America/Recife');

class DB_Connect {
    private $con;
 
    // Connecting to database
    public function connect() {
        require_once dirname(__FILE__) . '/db_config.php';
        
        // Connecting to mysql database
        $this->con = new mysqli(DB_SERVER, DB_USER, DB_PASSWORD, DB_DATABASE);
	$this->con->set_charset("utf8");
         
        // return database handler
        return $this->con;
    }
}

?>