/* 
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */


$(document).ready(function() {
  
   $('#formbean').on('submit', function(){
//       alert();
       var nom=$('#nom').val();
       


       chaine = JSON.stringify($('#formbean').serializeArray());
        alert(chaine);

       
       
        $.ajax({
                url: $(this).attr('action'), // le nom du fichier indiqué dans le formulaire
                type: $(this).attr('method'), // la méthode indiquée dans le formulaire (get ou post)
                data: 'beanjson='+$(this).serialize(), // je sérialise les données (voir plus loin), ici les $_POST
                success: function(html) { // je récupère la réponse du fichier PHP
                    alert('html oui'); // j'affiche cette réponse
                }
            });
            
        return false; // j'empêche le navigateur de soumettre lui-même le formulaire
   }); 
    
    
});