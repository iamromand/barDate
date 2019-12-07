jQuery( document ).ready(function($) {
    
    /*Add slick background to Homepage*/
    $('.full-background').slick({
        dots: false,
        infinite: true,
        speed: 500,
        fade: true,
        cssEase: 'linear',
        autoplay: true,
        autoplaySpeed: 2000,
    });

    /*Setup datepicker for birthday*/
    $('.dobInputText').datepicker({
      changeMonth: true,
      changeYear: true,
      yearRange: "-100:-18",
      minDate: new Date(1920,1-1,1),
      maxDate: '-18Y',
      defaultDate: new Date(2000,1-1,1),
      dateFormat: 'dd.mm.yy'
    }).mask('99.99.9999');
    
    /*Setup location picker and save data into inputs*/
    if ($(".yourinfo-location").length > 0) {
        var placesAutocomplete = places({
          appId: 'plMEPBFKLRPM',
          apiKey: '33a4501a18199d2ac6e477f17b8d9c57',
          container: document.querySelector('.yourinfo-location')
        });
        var changedLocation = false;
        placesAutocomplete.setVal($('.yourinfo-location-name').val());
        placesAutocomplete.on('change', function(e){
            if(e.suggestion != null){
                $('.yourinfo-location-lng').val(e.suggestion.latlng.lng);
                $('.yourinfo-location-lat').val(e.suggestion.latlng.lat);
                $('.yourinfo-location-name').val(e.suggestion.name);
                $('.yourinfo-location-country').val(e.suggestion.country);
                changedLocation = true;
            }
            else{
                changedLocation = false;            
            }
        });
    }
    
    /*Dont allow min and max ages to be unconsistent - test when max age changed (example - min age 29, max age 26)*/
    $('.select-max-age-in-profile-view').on('change', function(){
        var maxAge = $('.select-max-age-in-profile-view').val();
        var minAge = $('.select-min-age-in-profile-view').val();
        if(maxAge < minAge){
            $('.select-min-age-in-profile-view').val(maxAge);
        }
    });
    
    /*Dont allow min and max ages to be unconsistent - test when min age changed (example - min age 29, max age 26)*/
    $('.select-min-age-in-profile-view').on('change', function(){
        var maxAge = $('.select-max-age-in-profile-view').val();
        var minAge = $('.select-min-age-in-profile-view').val();
        if(maxAge < minAge){
            $('.select-max-age-in-profile-view').val(minAge);
        }
    });
    
    /*update backing input on range picker change*/
    $('.range-input-max-radius-in-profile-view').on('change', function(){
        $('.input-max-radius-in-profile-view').val($(this).val());
    });
    
    /*Add reminder to update profile image*/
    if($("body").hasClass("noImage")){
        helloBar("You don't have a profile image. You have better chances to find a date with a profile image. Please add it by clicking <a href='profileImage.xhtml'>here</a>", 'profilePicBar');
    }
    
    /*activate X button*/
    $(".hellobar-close a").on('click', function(e){
        e.preventDefault();
        $('.hellobar').hide();
    });
 
    /*maintanence for liking, unliking and reporting*/
    $('.no-param.search .person-l').remove();
    $('.no-param.search .person-l').css('display', 'block');
    if($('body.no-param.search .person-notl').length == 0){
        $('body.no-param.search .full-results').text("Your search results seem so empty. Our pool of users is still small, so we suggest you to expand your search criteria, or try again later.");
    }
 
    $('.likedPeople .person-notl').remove();
    $('.likedPeople .person-notl').css('display', 'block');
    if($('body.likedPeople .person-l').length == 0){
        $('body.likedPeople .full-results').text("You don't seem to like any members. We found the best way to utilize our site is to start liking people.");
    }
    
    $('.full-results .person-reported').remove();
    function isEmpty( el ){
      return !$.trim(el.html())
    }
    if (isEmpty($('.full-results'))) {
        $('.full-results').text("Our pool of users is growing by the day. Please keep searching and liking.");
    }
   
   /*maintenance to remove empty spaces that ocupied by errors*/
    $( ".table-all-info tr td span:empty" ).each(function( index ) {
        $(this).closest('tr').addClass('empty-container');
   });
   
});

/*add body class when new messages*/
function classNewMessages(){
    jQuery('body').addClass('newMessages');
    helloBar("You have new messages", 'messagesbar');
}

/*add hellobar to body*/
function helloBar(str, classOfBar){
    $(".hellobars").append("<div class='hellobar "+classOfBar+"' style='top: "+top+"px;'><div class='hello-bar-content'>"+str+"</div><div class='hellobar-close'><a href='#'>X</a></div></div>");
}

jQuery(window).load(function() {
    if($("body.chat").length > 0){
        jQuery("html, body").animate({ scrollTop: jQuery(document).height() }, 1000);      
    }
});