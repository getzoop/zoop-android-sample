# Zoop Client Sample

Se você é desenvolvedor (ou tem acesso à uma equipe de desenvolvimento) e deseja criar seu próprio aplicativo para cobranças com mPOS, você pode utilizar este código como base para sua solução.

Você pode fazer forks do projeto ou simplesmente criar seu próprio repositório com o conteúdo alterado. Como esta é uma solução open-source, queremos o apoio da comunidade. Correções e PRs são super bem vindos.

Por fim, o projeto utiliza nosso SDK de pagamentos Android. Caso note algum problema com seu funcionamento, entre em contato com nosso suporte através do e-mail: suporte@zoop.com.br.


## Configuração

Crie e adicione um arquivo `Credentials.kt` no caminho `/app/src/main/java/com/example/zoopclientsample/` do projeto. 

Este arquivo deve ser um `Object` respeitando o seguinte formato:

```
package com.example.zoopclientsample

object Credentials {

    const val MARKETPLACE_ID = {inserir aqui seu markteplaceId}

    const val SELLER_ID = {inserir aqui seu sellerId}

    const val PUBLISHABLE_KEY = {inserir aqui sua publishableKey}

    const val USER_TOKEN = {inserir aqui seu userToken}

}
```

Caso tenha dúvidas em relação a essas credenciais entrar em contato com o suporte. 


## Screenshots

<table>
  <tr>
    <td><img src="screenshots/Screenshot_20200415-165820.png" width=144 height=304></td>
    <td><img src="screenshots/Screenshot_20200415-165830.png" width=144 height=304></td>
    <td><img src="screenshots/Screenshot_20200415-165851.png" width=144 height=304></td>
  </tr>
  <tr>
    <td><img src="screenshots/Screenshot_20200415-165917.png" width=144 height=304></td>
    <td><img src="screenshots/Screenshot_20200415-165925.png" width=144 height=304></td>
    <td><img src="screenshots/Screenshot_20200415-165932.png" width=144 height=304></td>
  </tr>
  <tr>
    <td><img src="screenshots/Screenshot_20200415-165940.png" width=144 height=304></td>
    <td><img src="screenshots/Screenshot_20200415-170014.png" width=144 height=304></td>
    <td><img src="screenshots/Screenshot_20200415-170030.png" width=144 height=304></td>
  </tr>
 </table>


## Licença

ZoopClientSample está licenciada sob os termos da licença [MIT License](LICENSE) e está disponível gratuitamente.


## Links

* [Docs](https://docs.zoop.co/docs/sdk-android-1)
* [Suporte](suporte@zoop.com.br)
