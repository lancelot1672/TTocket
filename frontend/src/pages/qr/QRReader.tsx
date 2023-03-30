import {QrScanner} from '@yudiel/react-qr-scanner';

function QRReader (){      
    return(
        <QrScanner
          onDecode={(result) => alert(result)}
          onError={(error) => alert(error?.message)}
      />
    )
}

export default QRReader;