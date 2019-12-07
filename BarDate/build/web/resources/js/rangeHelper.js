// DOM Ready
$(function() {
    var el, newPoint, newPlace, offset, clicked = false;
    $("input[type='range']").on('change', function() {
        el = $(this);
       calculateProperBubbleLocation(el);
    }).trigger('change');


    $("input[type='range']").on('mousedown', function(){
       el = $(this);
       calculateProperBubbleLocation(el);
       clicked = true;
    });

    $("input[type='range']").on('mouseup', function(){
       el = $(this);
       calculateProperBubbleLocation(el);
       clicked = false;
    });

    if($('body').find("input[type='range']").length !== 0){
       console.log('found');
       $('body').on('mousemove', function(e){
           console.log(clicked);
           if(clicked){
               calculateProperBubbleLocation(el);    
           }
       });
    }



    function calculateProperBubbleLocation(el){
       // Cache this for efficiency


       // Measure width of range input
       width = el.width();

       // Figure out placement percentage between left and right of input
       newPoint = (el.val() - el.attr("min")) / (el.attr("max") - el.attr("min"));

       // Janky value to get pointer to line up better
       offset = -1.9;

       // Prevent bubble from going beyond left or right (unsupported browsers)
       if (newPoint < 0) { newPlace = 0; }
       else if (newPoint > 1) { newPlace = width; }
       else { newPlace = width * newPoint + offset; offset -= newPoint; }

       // Move bubble
       el
         .next("output")
         .css({
           left: newPlace,
           marginLeft: offset + "%"
         })
         .text(el.val());
    }
});
