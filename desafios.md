## 1 - Cadastro de um autor

### necessidades

    É necessário cadastrar um novo autor no sistema.
    Todo autor tem um nome, email e uma descrição.
    Também queremos saber o instante exato que ele foi registrado.

### restrições

    O instante não pode ser nulo
    O email é obrigatório
    O email tem que ter formato válido
    O nome é obrigatório
    A descrição é obrigatória e não pode passar de 400 caracteres

### resultado esperado

    Um novo autor criado e status 200 retornado

## 2 - Autor é único

### necessidades

    O email do autor precisa ser único no sistema

### resultado esperado

    Erro de validação no caso de email duplicado

## 3 - Nova categoria

### necessidades

    Toda categoria precisa de um nome

### restrições

    O nome é obrigatório
    O nome não pode ser duplicado

### resultado esperado

    Uma nova categoria cadastrada no sistema e status 200 retorno
    Caso alguma restrição não seja atendida, retorne 400 e um json informando os problemas de validação

## 4 - Um novo livro

### necessidades

    Um título
    Um resumo do que vai ser encontrado no livro
    Um sumário de tamanho livre.
      O texto deve entrar no formato markdown, que é uma string.
      Dessa forma ele pode ser formatado depois da maneira apropriada.
    Preço do livro
    Número de páginas
    Isbn(identificador do livro)
    Data que ele deve entrar no ar(de publicação)
    Um livro pertence a uma categoria
    Um livro é de um autor

### restrições

    Título é obrigatório
    Título é único
    Resumo é obrigatório e tem no máximo 500 caracteres
    Sumário é de tamanho livre.
    Preço é obrigatório e o mínimo é de 20
    Número de páginas é obrigatória e o mínimo é de 100
    Isbn é obrigatório, formato livre
    Isbn é único
    Data que vai entrar no ar precisa ser no futuro
    A categoria não pode ser nula
    O autor não pode ser nulo


### resultado esperado

    Um novo livro precisa ser criado e status 200 retornado
    Caso alguma restrição não seja atendida, retorne 400 e um json informando os problemas de validação