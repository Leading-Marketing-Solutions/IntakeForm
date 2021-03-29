var hash = window.location.pathname.substring(1);
var imgCounter = 1;
var maxImages = 10;

$(".imgAdd").click(function(){

  //TODO анализировать количество уже добавленных. И если их 12, то скрыть кнопку добавления
   // imgCounter++;
  //$(this).closest(".row").find('.imgAdd').before('<div class="col-sm-2 imgUp"><div class="imagePreview"></div><label class="btn btn-primary">Upload<input type="file" class="uploadFile img noLogo" id="img_' + imgCounter + '"  value="Upload Photo" style="width:0px;height:0px;overflow:hidden;"></label><i class="fa fa-times del"></i></div>');
  //$('#img_' + imgCounter).bind('change', function(){
  //  handleImgChange($(this));
  //});

  imgAdd(this);

});

function imgAdd(plusIcon)
{
    var delIcon = $('i.fa.fa-times.del');

    $(delIcon).remove();

    imgCounter++;

    $(plusIcon).closest(".row").find('.imgAdd').before('<div class="col-sm-2 imgUp"><div class="imagePreview"></div><label class="btn btn-primary">Upload<input type="file" class="uploadFile img noLogo" id="img_' + imgCounter + '"  value="Upload Photo" style="width:0px;height:0px;overflow:hidden;"></label><i class="fa fa-times del"></i></div>');

    $('#img_' + imgCounter).bind('change', function(){
      handleImgChange($(this));
    });

    if(imgCounter == maxImages)
    $('.fa.fa-plus.imgAdd').hide();

}

$(document).on("click", "i.del" , function() {

    var imageName = $(this).parent().find('.imagePreview').css('background-image');
    $(this).parent().remove();
    imgCounter--;

    $('.fa.fa-plus.imgAdd').show();

    console.log("deleting image before substring: " + imageName);
    imageName = imageName.substring(imageName.lastIndexOf('/') + 1).replace('")', '');
    console.log("deleting image after substring: " + imageName);

    $.ajax({
            type: "DELETE",
            url: "/api/image/" + imageName,
            success: function(){
                console.log("deleted successfully");
                $('.img_cont').find('.imgUp').last().append('<i class="fa fa-times del"></i>');
            }
        });

  //TODO. Если их стало 11 и кнопки добавления нет, то добавить


});
$(function() {
    $(document).on("change",".uploadFile", function()
    {
    		var uploadFile = $(this);
        var files = !!this.files ? this.files : [];
        if (!files.length || !window.FileReader) return; // no file selected, or no FileReader support

        if (/^image/.test( files[0].type))
        { // only image file
            var reader = new FileReader(); // instance of the FileReader
            reader.readAsDataURL(files[0]); // read the local file

            reader.onloadend = function()
            { // set image data as background of div
                //alert(uploadFile.closest(".upimage").find('.imagePreview').length);
                uploadFile.closest(".imgUp").find('.imagePreview').css("background-image", "url("+this.result+")");


                //TODO ajax uploading to server this image
            }
        }

    });
});
