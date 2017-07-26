$(document).ready(function() {

    /*
     UI handlers
     */
    $('.p').mask("###", {placeholder: "___"});

    $('#module_is_default').checkboxpicker({});


    /*
     Validation
     */
    $.validator.setDefaults({
        highlight: function(element) {
            $(element).closest('.form-group').addClass('has-error');
        },
        unhighlight: function(element) {
            $(element).closest('.form-group').removeClass('has-error');
        },
        errorElement: 'span',
        errorClass: 'help-block',
        errorPlacement: function(error, element) {
            if(element.parent('.input-group').length) {
                error.insertAfter(element.parent());
            } else if (element.attr('type') == 'file') {
                error.insertAfter(element.parent().parent().parent());
            } else {
                error.insertAfter(element);
            }
        }
    });
    var form = $("#module-form");
    form.validate({
        rules: {
            module_short_name: {
                required: true
            },
            module_start_priority: {
                required: true
            }
        }
    });
    form.submit(function (e) {
        e.preventDefault();

        if (form.valid()) {
            var formData = JSON.stringify($('#module-form').serializeObject());
            $.ajax({
                type: 'POST',
                url: POST_URL + "/" + $("#module_id").val(),
                data: formData,
                processData: false,
                contentType:"application/json; charset=utf-8",
                dataType:"json",
                success: function (response) {
                    if (response.responseCode === 0) {
                        $('#module-form').find('input, textarea, button, select').val('');
                        $('#module-form').find('input, textarea, button, select').attr('disabled', 'disabled');
                        $('#result').html('<div class="alert alert-dismissable alert-success"><a class="close" data-dismiss="alert" href="#" aria-hidden="true">×</a>'+response.responseText+'</div>');
                        setTimeout(function () {
                            window.location = REDIRECT_URL;
                        }, 1000);
                    }
                    else {
                        $('#result').html('<div class="alert alert-dismissable alert-danger"><a class="close" data-dismiss="alert" href="#" aria-hidden="true">×</a><strong>Error!</strong><br>'+response.responseText+'</div>');
                    }
                }
            });
        }

    });
});