import { test, expect } from '@playwright/test';

const casos = [
  { cep: '80000000', valor: '100,00', aceito: true, mensagem: 'Frete: R$ 15,00', classe: 'CEP iniciado por 8' },
  { cep: '89999999', valor: '100,00', aceito: true, mensagem: 'Frete: R$ 15,00', classe: 'último CEP iniciado por 8' },
  { cep: '79999999', valor: '100,00', aceito: true, mensagem: 'Frete: R$ 25,00', classe: 'CEP imediatamente abaixo de 8' },
  { cep: '90000000', valor: '100,00', aceito: true, mensagem: 'Frete: R$ 25,00', classe: 'CEP imediatamente acima de 8' },
  { cep: '01310100', valor: '100,00', aceito: true, mensagem: 'Frete: R$ 25,00', classe: 'demais CEPs' },
  { cep: '80000000', valor: '0,01', aceito: true, mensagem: 'Frete: R$ 15,00', classe: 'valor mínimo positivo' },
  { cep: '80000000', valor: '100.50', aceito: true, mensagem: 'Frete: R$ 15,00', classe: 'valor com ponto decimal' },
  { cep: '80000000', valor: '199,99', aceito: true, mensagem: 'Frete: R$ 15,00', classe: 'abaixo do limite de frete grátis (CEP 8)' },
  { cep: '01310100', valor: '199,99', aceito: true, mensagem: 'Frete: R$ 25,00', classe: 'abaixo do limite de frete grátis (demais CEPs)' },
  { cep: '80000000', valor: '200,00', aceito: true, mensagem: 'Frete grátis', classe: 'limite de frete grátis (CEP 8)' },
  { cep: '01310100', valor: '200', aceito: true, mensagem: 'Frete grátis', classe: 'limite de frete grátis (demais CEPs)' },
  { cep: '01310100', valor: '200,01', aceito: true, mensagem: 'Frete grátis', classe: 'acima do limite de frete grátis' },
  { cep: '80000000', valor: '0', aceito: false, mensagem: 'Dados inválidos', classe: 'valor zero' },
  { cep: '80000000', valor: '-10', aceito: false, mensagem: 'Dados inválidos', classe: 'valor negativo' },
  { cep: '80000000', valor: 'abc', aceito: false, mensagem: 'Dados inválidos', classe: 'valor com tipo inválido' },
  { cep: '80000000', valor: '10,999', aceito: false, mensagem: 'Dados inválidos', classe: 'valor com três casas decimais' },
  { cep: '80000000', valor: '', aceito: false, mensagem: 'Dados inválidos', classe: 'valor vazio' },
  { cep: '1234567', valor: '100,00', aceito: false, mensagem: 'Dados inválidos', classe: 'CEP com 7 dígitos' },
  { cep: '123456789', valor: '100,00', aceito: false, mensagem: 'Dados inválidos', classe: 'CEP com 9 dígitos' },
  { cep: 'abcdefgh', valor: '100,00', aceito: false, mensagem: 'Dados inválidos', classe: 'CEP com tipo inválido' },
  { cep: '8000000a', valor: '100,00', aceito: false, mensagem: 'Dados inválidos', classe: 'CEP misto com letra' },
  { cep: '', valor: '100,00', aceito: false, mensagem: 'Dados inválidos', classe: 'CEP vazio' },
  { cep: '', valor: '', aceito: false, mensagem: 'Dados inválidos', classe: 'todos os campos vazios' },
];

for (const caso of casos) {
  test(`CEP ${caso.cep || '(vazio)'} e valor ${caso.valor || '(vazio)'} — ${caso.classe}`, async ({ page }) => {
    await page.goto('/frete');
    await page.getByLabel('CEP').fill(caso.cep);
    await page.getByLabel('Valor do pedido').fill(caso.valor);
    await page.getByRole('button', { name: 'Calcular frete' }).click();

    const resultado = page.locator('#resultado');
    await expect(resultado).toBeVisible();
    await expect(resultado).toHaveText(caso.mensagem);
    await expect(resultado).toHaveAttribute('role', caso.aceito ? 'status' : 'alert');
  });
}