<?php

class DB_Functions {
 
    private $conn;
 
    // constructor
    function __construct() {
        require_once dirname(__FILE__) . '/db_connect.php';
        // connecting to database
        $db = new Db_Connect();
        $this->conn = $db->connect();
    }
 
    // destructor
    function __destruct() {
         
    }
 
    /**
     * Storing new user
     * returns user details
     */
    public function storeUser($nome, $sobrenome, $email, $senha) {
        $uuid = uniqid('', true);
        $hash = $this->hashSSHA($senha);
        $senha_encriptada = $hash["encrypted"]; // encrypted password
        $salt = $hash["salt"]; // salt
 
        $stmt = $this->conn->prepare("INSERT INTO USUARIO(unique_id, nome, sobrenome, email, senha_encriptada, salt, criado_em) VALUES(?, ?, ?, ?, ?, ?, NOW())");
        $stmt->bind_param("ssssss", $uuid, $nome, $sobrenome, $email, $senha_encriptada, $salt);
        $result = $stmt->execute();
        $stmt->close();
 
        // check for successful store
        if ($result) {
            $stmt = $this->conn->prepare("SELECT unique_id, nome, sobrenome, email, criado_em FROM USUARIO WHERE email = ?");
            $stmt->bind_param("s", $email);
            $stmt->execute();
            //$usuario = $stmt->get_result()->fetch_assoc();
            $result = get_result($stmt);
            $usuario = $result[0];
            $stmt->close();
 
            return $usuario;
        } else {
            return false;
        }
    }
    
    public function updateInterest($uid, $interesses) {
    
	//echo $uid;
        //echo $interesses;
        $stmt = $this->conn->prepare("UPDATE USUARIO SET interesses = ?, atualizado_em = NOW() WHERE unique_id = ?");
        $stmt->bind_param("ss", $interesses, $uid);
        $result = $stmt->execute();
        $stmt->close();
 
        // check for successful store
        if ($result) {
            return true;
        } else {
            return false;
        }
    }
 
    /**
     * Get user by email and password
     */
    public function getUserByEmailAndPassword($email, $senha) {
 
        $stmt = $this->conn->prepare("SELECT id, unique_id, nome, sobrenome, email, senha_encriptada, salt FROM USUARIO WHERE email = ?");
 
        $stmt->bind_param("s", $email);
 
        if ($stmt->execute()) {
            //$usuario = $stmt->get_result()->fetch_assoc();
            $result = get_result($stmt);
            $usuario = $result[0];
            $stmt->close();
 
            // verifying user password
            $salt = $usuario['salt'];
            $senha_encriptada = $usuario['senha_encriptada'];
            $hash = $this->checkhashSSHA($salt, $senha);
            // check for password equality
            if ($senha_encriptada == $hash) {
                // user authentication details are correct
                return $usuario;
            }
        } else {
            return NULL;
        }
    }
 
    /**
     * Check user is existed or not
     */
    public function isUserExisted($email) {
        $stmt = $this->conn->prepare("SELECT email FROM USUARIO WHERE email = ?");
 
        $stmt->bind_param("s", $email);
 
        $stmt->execute();
 
        $stmt->store_result();
 
        if ($stmt->num_rows > 0) {
            // user existed 
            $stmt->close();
            return true;
        } else {
            // user not existed
            $stmt->close();
            return false;
        }
    }
 
    /**
     * Encrypting password
     * @param password
     * returns salt and encrypted password
     */
    public function hashSSHA($senha) {
 
        $salt = sha1(rand());
        $salt = substr($salt, 0, 10);
        $encrypted = base64_encode(sha1($senha . $salt, true) . $salt);
        $hash = array("salt" => $salt, "encrypted" => $encrypted);
        return $hash;
    }
 
    /**
     * Decrypting password
     * @param salt, password
     * returns hash string
     */
    public function checkhashSSHA($salt, $senha) {
 
        $hash = base64_encode(sha1($senha . $salt, true) . $salt);
 
        return $hash;
    }
    
    /**
     * Get A event
     */
    
    public function getEventById($id) {
 
        $stmt = $this->conn->prepare("SELECT id, name, description, start_time, end_time, place, cover FROM EVENT WHERE id = ?");
        $stmt->bind_param("s", $id);
 
        if ($stmt->execute()) {
            $result = get_result($stmt);
            //print_r($result);
            $stmt->close();
            return $result;
        }
    }

    /**
     * Get all events
     */
    public function getEvents() {
 
        $stmt = $this->conn->prepare("SELECT id, nome, local, data, descricao, urlimg, adimg FROM EVENTO");      
 
        if ($stmt->execute()) {
            $result = get_result($stmt);
            //print_r($result);
            $stmt->close();
            return $result;
        }
    }
    
    public function getEventos() {
 
        $stmt = $this->conn->prepare("SELECT id, id_facebook, name, description, start_time, end_time, place, cover, category FROM EVENT");      
 
        if ($stmt->execute()) {
            $result = get_result($stmt);
            //print_r($result);
            $stmt->close();
            return $result;
        }
    }
    
    public function getInteresses() {
 
        $stmt = $this->conn->prepare("SELECT id, title, category FROM INTEREST");      
 
        if ($stmt->execute()) {
            $result = get_result($stmt);
            //print_r($result);
            $stmt->close();
            return $result;
        }
    }
    
    public function getUserInteresses($uid) {
 
        //echo $uid;
        $stmt = $this->conn->prepare("SELECT interesses FROM USUARIO WHERE unique_id = ?");
        $stmt->bind_param("s", $uid);      
 
        if ($stmt->execute()) {
            $result = get_result($stmt);
            //print_r($result[0]);
            $stmt->close();
            return $result[0];
        }
    }
    
    public function getAVGNote($evento) {
 
        $stmt = $this->conn->prepare("SELECT AVG(nota) as 'nota' FROM AVALIACAO WHERE idEvento = ?");
        $stmt->bind_param("s", $evento);
 
        if ($stmt->execute()) {
            $result = get_result($stmt);
            //print_r($result[0]["nota"]);
            $result = round($result[0]["nota"],1);
            $stmt->close();
            return $result;
        }
    }
    }
function get_result( $Statement ) {
    $RESULT = array();
    $Statement->store_result();
    for ( $i = 0; $i < $Statement->num_rows; $i++ ) {
        $Metadata = $Statement->result_metadata();
        $PARAMS = array();
        while ( $Field = $Metadata->fetch_field() ) {
            $PARAMS[] = &$RESULT[ $i ][ $Field->name ];
        }
        call_user_func_array( array( $Statement, 'bind_result' ), $PARAMS );
        $Statement->fetch();
    }
    return $RESULT;
}

 
?>