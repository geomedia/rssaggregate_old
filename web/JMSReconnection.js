/* 
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */


$(document).ready(function() {
    //Une petite fonction javascript pour permettant de lancer le processus de reconnection au serveur JMS et d'afficher les résultat en ajax dans l'entete du site
    var $bt = $('#jmsrecoBT');
    var $pinfoJMS = $('#pinfoJMS');
    $bt.on('click', function truc2() {
        $.ajax({
            url: '/RSSAgregate/config/jmsreload',
            data: '', // on envoie $_GET['id_region']
            dataType: 'text',
            success: function(txt) {
                if (txt !== "OK") {
                    $pinfoJMS.empty();
                    $pinfoJMS.append(txt);
                    $('#JMSstat').empty(); $('#JMSstat').append('<span class="erreur">ERREUR</span>');
                }
                else{
                    $('#JMSstat').empty(); 
                    $('#JMSstat').append('OK!');
                    $pinfoJMS.empty();
                    $bt.remove();
                }
            }
        });
    }
    );
});
