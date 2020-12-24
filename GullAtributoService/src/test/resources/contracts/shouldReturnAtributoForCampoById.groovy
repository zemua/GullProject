import org.springframework.cloud.contract.spec.Contract
Contract.make {
    description "should return atributo for campo when input is atributo id"
    request{
        method GET()
        url("/api/atributos/idforcampo/idaleatoria") {
        }
    }
    response {
        body("Even")
        status 200
    }
}