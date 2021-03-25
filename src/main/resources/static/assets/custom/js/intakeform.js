//alert("it works");

//todo
//here add ajax requests in loop
//var hash = "wsx123";//later get from html
var hash = window.location.pathname.substring(1);
var allIds = "";

$( ":input" ).each(function(){

    var id = $(this).attr('id');
    if((id != null)&&(id != 'logofile'))
    {
        $(this).on('focusout', function(){setValue(id);});

        getValue(id);
        allIds += id + ", ";
    }
});

getValue('logofile');



$("#logofile").change(function (){
       var fileName = $(this).val().split('\\').pop().replaceAll(/\s/g,'');
       //$(".filename").html(fileName);
       console.log(fileName);


       var fileData = $('#logofile').prop('files')[0];
       var formData = new FormData();
       formData.append('file', fileData);

        $.ajax({
             type: "POST",
             //beforeSend: function(request) {
             //    request.setRequestHeader(header, token);
             //  },
             url: "/api/logo",
             data: formData,
             processData: false,
             contentType: false,
             success: function(){
                //putValue('logofile', fileName);
                setValue('logofile', fileName);
             }
             //dataType: "json",
             //contentType : "application/json",

           });



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
      success: function(data){
        if(inputId == 'logofile')
            processLogo(data);
        else
            putValue(inputId, data);

      }
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
    setValue(inputId, null);
}

function setValue(inputId, value)
{
    if(value == null)
    {
        value = $('#' + inputId).val();
    }

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

function processLogo(logoName)
{
    if(logoName.length > 0)
    {
        console.log('need put logo ' + logoName);
        $('#logofile').closest(".imgUp").find('.imagePreview').css("background-image", "url(/api/logo/"+logoName+")");
    }
}
