document.getElementById("botonBorrar").addEventListener("click", function(event){
  event.preventDefault()
  if (document.getElementById("confirmaBorrar").value === "borrar"){
  	document.getElementById("myForm").submit(); 
  }
});