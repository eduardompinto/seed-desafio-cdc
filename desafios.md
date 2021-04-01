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