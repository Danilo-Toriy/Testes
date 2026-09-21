import { test, expect } from '@playwright/test';

const casos = [
  { senha: 'Abcde12', confirmacao: 'Abcde12', aceito: false, mensagem: 'Senha fora do padrão', classe: 'abaixo do mínimo (7 caracteres)' },
  { senha: 'Abcdef12', confirmacao: 'Abcdef12', aceito: true, mensagem: 'Senha cadastrada', classe: 'limite mínimo (8 caracteres)' },
  { senha: 'Abcdefg12', confirmacao: 'Abcdefg12', aceito: true, mensagem: 'Senha cadastrada', classe: 'acima do mínimo (9 caracteres)' },
  { senha: 'Abcdefghijklmnopq12', confirmacao: 'Abcdefghijklmnopq12', aceito: true, mensagem: 'Senha cadastrada', classe: 'abaixo do máximo (19 caracteres)' },
  { senha: 'Abcdefghijklmnopqr12', confirmacao: 'Abcdefghijklmnopqr12', aceito: true, mensagem: 'Senha cadastrada', classe: 'limite máximo (20 caracteres)' },
  { senha: 'Abcdefghijklmnopqr12x', confirmacao: 'Abcdefghijklmnopqr12x', aceito: false, mensagem: 'Senha fora do padrão', classe: 'acima do máximo (21 caracteres)' },
  { senha: 'abcdef12', confirmacao: 'abcdef12', aceito: false, mensagem: 'Senha fora do padrão', classe: 'sem letra maiúscula' },
  { senha: 'ABCDEF12', confirmacao: 'ABCDEF12', aceito: false, mensagem: 'Senha fora do padrão', classe: 'sem letra minúscula' },
  { senha: 'Abcdefgh', confirmacao: 'Abcdefgh', aceito: false, mensagem: 'Senha fora do padrão', classe: 'sem número' },
  { senha: 'Abcd ef12', confirmacao: 'Abcd ef12', aceito: false, mensagem: 'Senha fora do padrão', classe: 'com espaço no meio' },
  { senha: ' Abcdef12', confirmacao: ' Abcdef12', aceito: false, mensagem: 'Senha fora do padrão', classe: 'com espaço no início' },
  { senha: '', confirmacao: '', aceito: false, mensagem: 'Senha fora do padrão', classe: 'campos vazios' },
  { senha: 'Abcde12', confirmacao: 'Abcdef12', aceito: false, mensagem: 'Senha fora do padrão', classe: 'formato inválido e confirmação divergente' },
  { senha: 'Abcdef12', confirmacao: 'Abcdef13', aceito: false, mensagem: 'As senhas não coincidem', classe: 'confirmação divergente' },
  { senha: 'Abcdef12', confirmacao: 'abcdef12', aceito: false, mensagem: 'As senhas não coincidem', classe: 'confirmação com diferença de maiúscula' },
  { senha: 'Abcdef12', confirmacao: '', aceito: false, mensagem: 'As senhas não coincidem', classe: 'confirmação vazia' },
];

for (const caso of casos) {
  test(`senha ${caso.senha || '(vazia)'} — ${caso.classe}`, async ({ page }) => {
    await page.goto('/senha');
    await page.getByLabel('Nova senha').fill(caso.senha);
    await page.getByLabel('Confirmar senha').fill(caso.confirmacao);
    await page.getByRole('button', { name: 'Cadastrar senha' }).click();

    const resultado = page.locator('#resultado');
    await expect(resultado).toBeVisible();
    await expect(resultado).toHaveText(caso.mensagem);
    await expect(resultado).toHaveAttribute('role', caso.aceito ? 'status' : 'alert');
  });
}

test('limpa os campos após cadastrar senha válida', async ({ page }) => {
  await page.goto('/senha');
  await page.getByLabel('Nova senha').fill('Abcdef12');
  await page.getByLabel('Confirmar senha').fill('Abcdef12');
  await page.getByRole('button', { name: 'Cadastrar senha' }).click();

  await expect(page.locator('#resultado')).toHaveText('Senha cadastrada');
  await expect(page.getByLabel('Nova senha')).toHaveValue('');
  await expect(page.getByLabel('Confirmar senha')).toHaveValue('');
});