<?php

namespace App\Service;

use Mailjet\Client;
use Mailjet\Resources;

class MailjetService
{
    private $apiKey="8a45ce76c702f1f849ffdbb5a6acc6af" ;  // Your MailJet API Key here
    private $secretKey="4e6fe995e3cf64c2d79669127e85694f";    // Your MailJet Secret Key here
    public function sendMail( $content,$toEmail,$toName):void
    {
        $mj= new Client($this->apiKey,$this->secretKey,true,['version' => 'v3.1']);
        $body = [
            'Messages' => [
                [
                    'From' => [
                        'Email' => "tesnim.satouri@esprit.tn",
                        'Name' => "Tesnim Satouri"
                      ],
                    'To' => [
                        [
                            'Email' => $toEmail,
                            'Name' => $toName,
                        ]
                    ],
                    'TemplateID' => 5750978,
                    'TemplateLanguage' => true,
                    'Variables' => [
 
                        "content" => $content,
 
 
                    ]
                ]
            ]
        ];
        $response = $mj->post(Resources::$Email, ['body' => $body]);
        $response->success() && var_dump($response->getData());
       
    }
    
}

?>