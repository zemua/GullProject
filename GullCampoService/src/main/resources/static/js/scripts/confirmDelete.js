(function () {

  var form = document.getElementById("myForm");
  var texto = document.getElementById("confirmaBorrar");
  var button = document.getElementById("botonBorrar");

  button.addEventListener("click", function(event){
    event.preventDefault()
    if (texto.value === "borrar"){
      form.submit(); 
    }
  });

  form.addEventListener("submit", function(event){
    event.preventDefault()
    if (texto.value === "borrar"){
      form.submit(); 
    }
  });

})();
