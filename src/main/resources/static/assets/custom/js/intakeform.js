//alert("it works");

//todo
//here add ajax requests in loop
//var hash = "wsx123";//later get from html
var hash = window.location.pathname.substring(1);

var allIds = "";

$( ":input" ).each(function(){

    var id = $(this).attr('id');
    if(id != null)
    {
        $(this).on('focusout', function(){setValue(id);});

        getValue(id);
        allIds += id + ", ";
    }
});

function getValue(inputId)
{
 var formData = new Object();
    formData.hash = hash;
    formData.field = inputId;

    $.ajax({
      type: "POST",
      //beforeSend: function(request) {
      //    request.setRequestHeader(header, token);
      //  },
      url: "/api/get",
      data: formData,
      success: function(data){putValue(inputId, data);}
      //dataType: "json",
      //contentType : "application/json",

    });
}

function putValue(inputId, value)
{
    $('#' + inputId).val(value);
}

function setValue(inputId)
{
    console.log("focus out at input " + inputId);

    var value = $('#' + inputId).val();
    if(value.length > 0)
    {

    var formData = new Object();
        formData.hash = hash;
        formData.field = inputId;
        formData.value = value;

        $.ajax({
          type: "POST",
          //beforeSend: function(request) {
          //    request.setRequestHeader(header, token);
          //  },
          url: "/api/set",
          data: formData,
          success: function(data){console.log("saved " + inputId + " = " + formData.value + ": " + data);}
          //dataType: "json",
          //contentType : "application/json",

        });
    }

}

