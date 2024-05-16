<?php

namespace App\Controller;

use Symfony\Component\Routing\Annotation\Route;
use Endroid\QrCode\Encoding\Encoding;
use Endroid\QrCode\ErrorCorrectionLevel;

use Endroid\QrCode\QrCode;
use Endroid\QrCode\Writer\PngWriter;
use Symfony\Bundle\FrameworkBundle\Controller\AbstractController;
use Symfony\Component\HttpFoundation\Response;

class QrcodeController extends AbstractController
{
    #[Route('/generate_qr_code/{Type}/{Date_remise}', name: 'generate_qr_code')]
    public function generateQrCode($Type, $Date_remise,): Response
    {
        // Concatenate product information into a single string
        $productInfo = "Type: $Type\nDate remise: $Date_remise\n";

        // Create the QR code using the concatenated product information
        $qrCode = new QrCode($productInfo);
        $qrCode->setErrorCorrectionLevel(ErrorCorrectionLevel::High);
        $qrCode->setMargin(10);
        $qrCode->setEncoding(new Encoding('UTF-8'));
        $qrCode->setSize(300);

        // Generate the QR code image
        $writer = new PngWriter();
        $result = $writer->write($qrCode);

        // Return the QR code image as a response
        return new Response($result->getString(), 200, [
            'Content-Type' => 'image/png',
        ]);
    }

    #[Route('/generate_qr_code/{Image}/{Titre}/{Description}/{Date_debut}/{Date_fin}', name: 'generate_qr_code_Campaign')]
    public function generateQrCode_Campaign($Titre ,$Image, $Date_debut,$Date_fin,$Description): Response
    {
        // Concatenate product information into a single string
        $productInfo = "Titre:$Titre \n Image: $Image\n  Description: $Description Date_debut: \n   $Date_debut\n Date_fin : $Date_fin \n ";

        // Create the QR code using the concatenated product information
        $qrCode = new QrCode($productInfo);
        $qrCode->setErrorCorrectionLevel(ErrorCorrectionLevel::High);
        $qrCode->setMargin(10);
        $qrCode->setEncoding(new Encoding('UTF-8'));
        $qrCode->setSize(300);

        // Generate the QR code image
        $writer = new PngWriter();
        $result = $writer->write($qrCode);

        // Return the QR code image as a response
        return new Response($result->getString(), 200, [
            'Content-Type' => 'image/png',
        ]);
    }
}