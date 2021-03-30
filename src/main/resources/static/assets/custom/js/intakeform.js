//todo
//here add ajax requests in loop
var hash = window.location.pathname.substring(1);
var allIds = "";

$(document).ready(function(){

    //rendering logo is it's already added
    getValue('logofile');

    //rendering images if they are already added
    getImages(hash);

    if($('#intakeform').hasClass('disabled_form'))
        disableForm();
});

//adding already inputed data to inputs
$( ":input" ).each(function(){

    var id = $(this).attr('id');
    //if((id != null)&&(id != 'logofile'))
    if((id != null)&&($(this).attr('type') != 'file'))
    {
        $(this).on('focusout', function(){setValue(id);});

        getValue(id);
        allIds += id + ", ";
    }
});




//saving selected logo image
$("#logofile").change(function (){
       var fileName = $(this).val().split('\\').pop().replaceAll(/\s/g,'');
       //$(".filename").html(fileName);
       console.log(fileName);


       var fileData = $('#logofile').prop('files')[0];
       var formData = new FormData();
       formData.append('file', fileData);
       formData.append('hash', hash);

        $.ajax({
             type: "POST",
             //beforeSend: function(request) {
             //    request.setRequestHeader(header, token);
             //  },
             url: "/api/logo",
             data: formData,
             processData: false,
             contentType: false,
             success: function(data){
                //putValue('logofile', fileName);
                setValue('logofile', data);
             }
             //dataType: "json",
             //contentType : "application/json",

           });
});


//event on image input change
$(".noLogo").on('change', function (){
   handleImgChange($(this));
});


//saving image to server
function handleImgChange(elem)
{
    var fileName = $(elem).val().split('\\').pop().replaceAll(/\s/g,'');
       console.log("image name: " + fileName);

       var fileData = $(elem).prop('files')[0];
       var formData = new FormData();
       var htmlId = $(elem).attr('id')
       formData.append('file', fileData);
       formData.append('hash', hash);
       formData.append('htmlId', htmlId);

       $.ajax({
           type: "POST",
           url: "/api/image",
           data: formData,
           processData: false,
           contentType: false,
           success: function(data){
              setValue(htmlId, data);
           }
      });

}

//getting value for input by input id from server
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

//putting value to input
function putValue(inputId, value)
{
    $('#' + inputId).val(value);
}


function setValue(inputId)
{
    setValue(inputId, null);
}


//saving value from input to server
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


//special method for rendering logo image
function processLogo(logoName)
{
    if(logoName.length > 0)
    {
        console.log('need put logo ' + logoName);
        $('#logofile').closest(".imgUp").find('.imagePreview').css("background-image", "url(/api/logo/"+logoName+")");
    }
}

function getImages(hash)
{
    $.ajax({
        type: "GET",
        url: "/api/images/" + hash,
        async: false,
        success: function(data){
            //console.log("all images: " + data);
            fillImages(data);
        }
        //dataType: "json",
        //contentType : "application/json",

    });
}

function fillImages(images)
{
    var counter = 1;

    images.forEach(function(item){

        $('#img_' + counter).closest(".imgUp").find('.imagePreview').css("background-image", "url(/api/image/"+item+")");


        if(counter < images.length)
        {
            imgAdd($('#img_' + counter));
            counter++;
        }

    });

}

//----------------------------------

(function () {
  'use strict'

  // Fetch all the forms we want to apply custom Bootstrap validation styles to
  var forms = document.querySelectorAll('.needs-validation')

  // Loop over them and prevent submission
  Array.prototype.slice.call(forms)
    .forEach(function (form) {
      form.addEventListener('submit', function (event) {
        if (!form.checkValidity()) {
          event.preventDefault()
          event.stopPropagation()
          //form.classList.remove('was-validated');
          alert("please input all required fields!");
          form.classList.add('was-validated');
        }
        else
        {
            form.classList.add('was-validated');
            if(confirm("Are you sure that you completed this form? After confirm you cannot change data!"))
                submitForm();
            event.preventDefault();
        }




      }, false)
    })
})()

//--------------------------------

function submitForm()
{
    disableForm();

    $.ajax({
            type: "POST",
            url: "/api/" + hash + "/submit",
            success: function(data){
                //console.log("all images: " + data);
                //fillImages(data);
                alert("thank you. Form is validated ans sent to verification!");
            }
            //dataType: "json",
            //contentType : "application/json",

        });

}


function disableForm()
{
    $("#intakeform :input").prop("disabled", true);
    $("i.imgAdd").hide();
    $("i.del").hide();
//todo
    //hide buttons add and delete image
}

