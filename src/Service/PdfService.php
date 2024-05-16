<?php

namespace App\Service;

use Dompdf\Dompdf;
use Dompdf\Options;

class PdfService
{
    private $domPdf;

    public function __construct($defaultFont = 'Arial') {
        $this->domPdf = new Dompdf();

        $pdfOptions = new Options();
        $pdfOptions->set('defaultFont', $defaultFont);
        $pdfOptions->set('isRemoteEnabled', true); // Enable remote file access for Dompdf
        $pdfOptions->set('isPhpEnabled', true); // Enable PHP code inside HTML that Dompdf parses

        $this->domPdf->setOptions($pdfOptions);
    }

    // Generates and sends a PDF to the browser
    public function showPdfFile($html) {
        $this->domPdf->loadHtml($html);
        $this->domPdf->render();
        $this->domPdf->stream("details.pdf", [
            'Attachment' => true
        ]);
    }

    // Generates a binary PDF content
    public function generateBinaryPDF($html) {
        $this->domPdf->loadHtml($html);
        $this->domPdf->render();
        return $this->domPdf->output();
    }

    // Generates a PDF for the donation list and optionally sends it to the browser
    public function generateDonListPDF($html, $streamToBrowser = true) {
        $this->domPdf->loadHtml($html);
        $this->domPdf->render();

        if ($streamToBrowser) {
            $this->domPdf->stream("don_list.pdf", [
                'Attachment' => true
            ]);
        } else {
            $pdfOutput = $this->domPdf->output();
            // You can save $pdfOutput to a file, store it in a database, or further process it as needed
        }
    }
}
