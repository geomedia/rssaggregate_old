/* 
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */


$(document).ready(function() {

    $('#form').on('submit', function truc2() {
        $.ajax({
            url: $(this).attr('action'), // le nom du fichier indiqué dans le formulaire
            type: $(this).attr('method'), // la méthode indiquée dans le formulaire (get ou post)
            data: $(this).serialize(), // je sérialise les données (voir plus loin), ici les $_POST
            dataType: 'json',
            success: function(html) {
                $('#container').highcharts(html);
//                alert(html);
//                alert('succes');
            }

        });
        return false;
    });

});