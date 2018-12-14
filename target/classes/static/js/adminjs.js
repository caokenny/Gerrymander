$('#addBtn').on('click', function () {
    $('#registerDiv').css("display", "block");
});

$('.closeRegisterBox').on('click', function () {
    $('#registerDiv').css("display", "none");
});

$('.deleteBtn').on('click', function () {
    var user = $(this).attr('id');
    var confirmed = confirm("Click Okay to confirm deletion of " + user);
    if (confirmed) {
        $.ajax({
            url: "/secure/delete",
            async: false,
            data: {"username" : user},
            type: "POST",
            success: function () {
                window.location.reload();
            }
        })
    }
});