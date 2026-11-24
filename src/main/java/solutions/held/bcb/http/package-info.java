/**
 * Infraestrutura HTTP interna da SDK de dados econômicos do Banco Central.
 *
 * <p>
 *     Contém o transporte HTTP resiliente ({@link solutions.held.bcb.http.HttpTransport}), o
 *     limitador de vazão ({@link solutions.held.bcb.http.Throttler}), o construtor de query string
 *     do SGS ({@link solutions.held.bcb.http.QueryParams}) e o construtor de URLs OData
 *     ({@link solutions.held.bcb.http.ConstrutorOData}). As classes deste pacote são de uso interno.
 * </p>
 *
 * @author Guilherme A. Held
 * @since 24/11/2026
 */
package solutions.held.bcb.http;
