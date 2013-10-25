/* 
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */


$(document).ready(function() {

    $('#form').on('submit', function truc2() {
        
        // Si il sélectionner toutes les items de la liste 2
        
//                    var $fluxSelection2 = $('#fluxSelection2');
            options = $('#fluxSelection2 option');

            //on force la sélection dans la liste 2 afin de pouvoir utiliser la fonction val() sur ce composant html
            for (i = 0; i < options.length; i++) {
                options[i].setAttribute('selected', 'true');
            }
            
        
        
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